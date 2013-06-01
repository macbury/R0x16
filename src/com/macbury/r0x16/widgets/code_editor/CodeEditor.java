package com.macbury.r0x16.widgets.code_editor;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Mouse;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.macbury.r0x16.Core;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.widgets.JavaScriptScanner;
import com.macbury.r0x16.widgets.JavaScriptScanner.Kind;

public class CodeEditor extends Widget {
  static private final char BACKSPACE = '\b';
  static private final char ENTER_DESKTOP = '\r';
  static private final char ENTER_ANDROID = '\n';
  static private final char TAB = '\t';
  static private final char DELETE = 127;
  static private final char BULLET = 149;
  ShapeRenderer shape;
  CodeEditorStyle style;
  private final static int GUTTER_PADDING = 10;
  private static final String TAG = "CodeEditor";
  private static final float LINE_PADDING = 2;
  
  private ArrayList<Line> lines;
  boolean disabled;
  private String text           = "";

  private Caret caret;
  private float blinkTime       = 0.32f;
  
  private HashMap<JavaScriptScanner.Kind, Color> styles;
  
  private long lastBlink;
  private boolean cursorOn;
  private Clipboard clipboard;
  private ClickListener inputListener;
  KeyRepeatTask keyRepeatTask = new KeyRepeatTask();
  float keyRepeatInitialTime  = 0.4f;
  float keyRepeatTime         = 0.1f;
  private Slider scrollbar;
  private Rectangle scissors;
  private Rectangle clipBounds;
  
  class KeyRepeatTask extends Task {
    int keycode;

    public void run () {
      inputListener.keyDown(null, keycode);
    }
  }
  
  public CodeEditor(Skin skin) {
    style  = skin.get(CodeEditorStyle.class);
    lines  = new ArrayList<Line>();
    styles = new HashMap<JavaScriptScanner.Kind, Color>();
    
    styles.put(JavaScriptScanner.Kind.KEYWORD, new Color(252.0f/255.0f, 128.0f/255.0f, 58.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.NORMAL, Color.WHITE);
    styles.put(JavaScriptScanner.Kind.STRING, new Color(142.0f/255.0f, 198.0f/255.0f, 95.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.COMMENT, new Color(95.0f/255.0f, 90.0f/255.0f, 96.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.NUMBER, new Color(127.0f/255.0f, 197.0f/255.0f, 120.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.SPECIAL_KEYWORD, new Color(252.0f/255.0f, 168.0f/255.0f, 98.0f/255.0f, 1.0f));
    shape     = new ShapeRenderer();
    this.text = "";
    this.parse(this.text);
    this.clipboard = Gdx.app.getClipboard();
    setWidth(getPrefWidth());
    setHeight(getPrefHeight());
    
    caret = new Caret(this.lines);
    initializeKeyboard();
    
    scrollbar = new Slider(0, 100, 1, true, skin);
    scrollbar.setWidth(16);
    scrollbar.setValue(100);
    
    this.scissors   = new Rectangle();
    this.clipBounds = new Rectangle(0,0,0,0);
  }
  
  private void initializeKeyboard() {
    addListener(inputListener = new ClickListener() {
      
      @Override
      public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        super.enter(event, x, y, pointer, fromActor);
        if (pointer == -1) {
          Core.shared().setCurrentCursor(Core.CURSOR_TEXT);
        }
      }

      @Override
      public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.exit(event, x, y, pointer, toActor);
        if (pointer == -1) {
          Core.shared().setCurrentCursor(Core.CURSOR_NORMAL);
        }
      }

      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        if (!super.touchDown(event, x, y, pointer, button)) return false;
        if (pointer == 0 && button != 0) return false;
        if (disabled) return true;
        caret.clearSelection();
        caret.setCursorPosition(xToCol(x) + caret.getColScrollPosition(), yToRow(y) + caret.getRowScrollPosition());
        Stage stage = getStage();
        if (stage != null) stage.setKeyboardFocus(CodeEditor.this);
        Core.shared().setCurrentCursor(Core.CURSOR_TEXT);
        return true;
      }
      
      public void touchDragged (InputEvent event, float x, float y, int pointer) {
        super.touchDragged(event, x, y, pointer);
        lastBlink        = 0;
        cursorOn         = false;
        int col          = xToCol(x) + caret.getColScrollPosition();
        int row          = yToRow(y) + caret.getRowScrollPosition();
        boolean moveLeft = col - caret.getCol() <= 0;
        boolean moveDown = row - caret.getRow() <= 0;
        caret.setCursorPosition(col,row);
        
        if (moveLeft) {
          updateScrollInLeftDirectionForCol();
        } else {
          updateScrollInRightDirectionForCol();
        }
        
        if (moveDown) {
          updateScrollInDownDirectionForRow();
        } else {
          updateScrollInUpDirectionForRow();
        }
        
        caret.startSelection();
      }
      
      public boolean keyDown(InputEvent event, int keycode) {
        return onKeyDown(event, keycode);
      }
      
      public boolean keyUp (InputEvent event, int keycode) {
        if (disabled) return false;
        keyRepeatTask.cancel();
        return true;
      }
      
      public boolean keyTyped (InputEvent event, char character) {
        return onKeyTyped(event, character);
      }
    });
  }

  

  private String getAllText() {
    String out = "";
    for (int i = 0; i < this.lines.size(); i++) {
      Line line = this.lines.get(i);
      out += line.getCachedFullText();
      if (i != this.lines.size() - 1) {
        out += '\n';
      }
    }
    
    return out;
  }

  private String buildStringFromLines() {
    String s = "";
    for (int i = 0; i < this.lines.size(); i++) {
      Line line = lines.get(i);
      s += line.getCachedFullText();
      if (i != this.lines.size() -1) {
        s+= "\n";
      }
    }
    return s;
  }

  protected boolean onKeyTyped(InputEvent event, char character) {
    if (disabled) return false;
    Stage stage = getStage();
    if (stage != null && stage.getKeyboardFocus() == this) {
      if (character == TAB) {
        caret.clearSelection();
        insertText("  ");
        caret.incCol(2);
      } else if (character == DELETE) {
        deleteRight();
      } else if (character == BACKSPACE) {
        delete();
      } else if (character == ENTER_DESKTOP) {
        insertText("\n");
        caret.incRow();
        int spaces = caret.getPrevPadding();
        Gdx.app.log(TAG, "Spaces in last line: "+ spaces);
        caret.setCol(0);
        for (int i = 0; i < spaces; i++) {
          insertText(" ");
        }
        caret.setCol(spaces);
        updateScrollInDownDirectionForRow();
      } else if (getFont().containsCharacter(character)) {
        insertText(String.valueOf(character));
        caret.incCol(1);
        updateScrollInLeftDirectionForCol();
      } else {
        return false;
      }
      
      return true;
    }
    return false;
  }

  protected boolean onKeyDown(InputEvent event, int keycode) {
    if (disabled) return false;
    lastBlink             = 0;
    cursorOn              = false;
    Stage stage           = getStage();
    
    if (stage != null && stage.getKeyboardFocus() == this) {
      boolean repeat = false;
      boolean ctrl   = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
      boolean shift  = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)   || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
      
      if (ctrl) {
        if (keycode == Keys.V) {
          paste();
          return true;
        }
        if (keycode == Keys.C || keycode == Keys.INSERT) {
          copy();
          return true;
        }
        if (keycode == Keys.X || keycode == Keys.DEL) {
          cut();
          return true;
        }
      }
      
      if (keycode == Keys.LEFT) {
        if (shift) {
          caret.startSelection();
        }
        if (ctrl) {
          caret.moveByWordInLeft();
        } else {
          if (caret.haveSelection() && !shift) {
            caret.clearSelection();
          } else {
            caret.moveOneCharLeft();
          }
        }
        
        updateScrollInRightDirectionForCol();
        
        repeat = true;
      }
      
      if (keycode == Keys.RIGHT) {
        if (shift) {
          caret.startSelection();
        }
        if (ctrl) {
          caret.moveByWordInRight();
        } else {
          if (caret.haveSelection() && !shift) {
            caret.clearSelection();
          } else {
            caret.moveOneCharRight();
          }
        }
        
        updateScrollInLeftDirectionForCol();
        repeat = true;
      }
      
      if (keycode == Keys.UP && caret.getRow() > 0) {
        if (shift) {
          caret.startSelection();
        } else {
          caret.clearSelection();
        }
        caret.moveRowUp();
        updateScrollInRightDirectionForCol();
        updateScrollInUpDirectionForRow();
        repeat = true;
      }
      
      if (keycode == Keys.DOWN && caret.getRow() < this.lines.size() - 1) {
        if (shift) {
          caret.startSelection();
        } else {
          caret.clearSelection();
        }
        caret.moveRowDown();
        updateScrollInRightDirectionForCol();
        updateScrollInDownDirectionForRow();
        repeat = true;
      }
      
      if (keycode == Keys.HOME) {
        if (shift) {
          caret.startSelection();
        }
        caret.setColHome();
        updateScrollInRightDirectionForCol();
      }
      
      if (keycode == Keys.END) {
        if (shift) {
          caret.startSelection();
        }
        caret.setColEnd();
        updateScrollInLeftDirectionForCol();
      }
      
      if (repeat && (!keyRepeatTask.isScheduled() || keyRepeatTask.keycode != keycode)) {
        keyRepeatTask.keycode = keycode;
        keyRepeatTask.cancel();
        Timer.schedule(keyRepeatTask, keyRepeatInitialTime, keyRepeatTime);
      }
      
      return true;
    } else {
      return false;
    }
  }
  
  private void updateScrollInUpDirectionForRow() {
    if (caret.getRow() < caret.getRowScrollPosition()) {
      caret.setRowScrollPosition(caret.getRow());
    }
  }

  private void updateScrollInDownDirectionForRow() {
    if (caret.getRow() >= visibleLinesCount()) {
      caret.setRowScrollPosition(caret.getRow() + 1 - visibleLinesCount());
    }
  }

  private void updateScrollInRightDirectionForCol() {
    if (caret.getCol() < caret.getColScrollPosition()) {
      caret.setColScrollPosition(caret.getCol());
    }
  }


  private void updateScrollInLeftDirectionForCol() {
    if (caret.getCol() > visibleCharsCount()-2) {
      caret.setColScrollPosition(caret.getCol() - visibleCharsCount()+1);
    } else {
      caret.setColScrollPosition(0);
    }
  }
  
  public void insertText(String ins) {
    if (caret.haveSelection()) {
      delete();
    }
    
    String lineText  = getAllText();
    int pos = caret.getCaretPosition();
    
    String finalText = lineText.substring(0, pos) + ins;
    if (pos < lineText.length()) {
      finalText += lineText.substring(pos, lineText.length());
    }
    
    parse(finalText);
  }
  
  private void deleteRight() {
    remove(1);
  }
  
  private void remove(int i) {
    String lineText  = getAllText();
    int pos          = caret.getCaretPosition();
    
    if (caret.haveSelection()) {
      int startPos = caret.getSelectionCaretPosition();
      
      int from = Math.min(pos, startPos);
      int to   = Math.max(pos, startPos);
      
      String finalText = lineText.substring(0, from);
      if (pos < lineText.length()) {
        finalText += lineText.substring(to, lineText.length());
      }
      caret.moveToSelectionStart();
      caret.clearSelection();
      parse(finalText);
    } else {
      String finalText = null;
      if (i == -1) {
        finalText = lineText.substring(0, pos + i);
        if (pos < lineText.length()) {
          finalText += lineText.substring(pos, lineText.length());
        }
        caret.moveOneCharLeft();
      } else {
        finalText = lineText.substring(0, pos);
        if (pos + 1 < lineText.length()) {
          finalText += lineText.substring(pos+ i, lineText.length());
        }
        //caret.moveOneCharRight();
      }
      
      parse(finalText);
    }
  }
  
  private void delete() {
    remove(-1);
  }

  private void cut() {
    if (caret.haveSelection()) {
      copy();
      delete();
    }
  }

  private void copy() {
    String lineText  = getAllText();
    int pos          = caret.getCaretPosition();
    
    if (caret.haveSelection()) {
      int startPos = caret.getSelectionCaretPosition();
      
      int from = Math.min(pos, startPos);
      int to   = Math.max(pos, startPos);
      
      String copyText = lineText.substring(from, to);
      clipboard.setContents(copyText);
    }
  }

  private void paste() {
    String content = clipboard.getContents();
    
    if (content != null) {
      insertText(content);
      caret.moveToSelectionStart();
      caret.clearSelection();
    }
  }

  public int xToCol(float x) {
    int c = (int) Math.floor((x - gutterWidth() - GUTTER_PADDING) / getFont().getSpaceWidth());
    if (c < 0) {
      c = 0;
    }
    return c;
  }
  
  public int yToRow(float y) {
    int r = (int) Math.floor((getHeight() - y) / getLineHeight());
    if (r < 0) {
      r = 0;
    }
    return r;
  }

  public float getLineHeight() {
    return getFont().getLineHeight() + LINE_PADDING;
  }
  
  private int gutterWidth() {
    return 40;
  }
  
  private int visibleLinesCount() {
    return (int) (this.getHeight() / getLineHeight());
  }
  
  private int visibleCharsCount() {
    return (int) ((this.getWidth()- gutterWidth() - GUTTER_PADDING) / getFont().getSpaceWidth());
  }
  
  @Override
  public void draw(SpriteBatch renderBatch, float parentAlpha) {
    Stage stage     = getStage();
    boolean focused = stage != null && stage.getKeyboardFocus() == this;
    
    final BitmapFont font      = getFont();
    final Drawable cursorPatch = style.cursor;

    Color color = getColor();
    float sx = getX();
    float sy = getY();
    float width = getWidth();
    float height = getHeight();

    int fromLine = caret.getRowScrollPosition();
    int toLine   = Math.min(fromLine + visibleLinesCount(), this.lines.size());
    
    int fromChar = caret.getColScrollPosition();
    int toChar   = fromChar + visibleCharsCount();
    
    int xOffset = (int) (caret.getColScrollPosition() * font.getSpaceWidth());
    
    renderBatch.end();

    shape.setProjectionMatrix(renderBatch.getProjectionMatrix());
      
    shape.begin(ShapeType.Filled);
    shape.setColor(0.1f, 0.1f, 0.1f, 1);
    shape.rect(sx, sy, width, height);
    shape.end();
    
    shape.begin(ShapeType.Filled);
    shape.setColor(0.25f, 0.25f, 0.25f, 1.0f);
    shape.rect(sx, sy, gutterWidth() + GUTTER_PADDING / 2 , height);
    shape.end();
    
    /*shape.begin(ShapeType.Line);
    shape.setColor(0.3f, 0.3f, 0.3f, 1);
    shape.rect(sx, sy, width, height);
    shape.end();*/
    

    clipBounds.set(sx+gutterWidth()+GUTTER_PADDING, sy, width-(gutterWidth()+GUTTER_PADDING), height);
    ScissorStack.calculateScissors(stage.getCamera(), renderBatch.getTransformMatrix(), clipBounds, scissors);
    ScissorStack.pushScissors(scissors);
    
    if (caret.haveSelection()) {
      int cursorRowStart = Math.min(caret.getSelectionStartRow(), caret.getRow());
      int cursorRowEnd   = Math.max(caret.getRow(), caret.getSelectionStartRow());
      
      int cursorColEnd   = caret.getCol();
      int cursorColStart = caret.getSelectionStartCol();
      if (caret.getRow() < caret.getSelectionStartRow()) {
        cursorColEnd   = caret.getSelectionStartCol();
        cursorColStart = caret.getCol();
      }
      
      Gdx.gl.glEnable(GL10.GL_BLEND);
      Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
      
      shape.begin(ShapeType.Filled);
      shape.setColor(1.0f, 1.0f, 1.0f, 0.1f);
      
      if (cursorRowStart == cursorRowEnd) {
        shape.rect(sx + gutterWidth() + ((cursorColStart+1) * getFont().getSpaceWidth()) - xOffset, 
            (sy + height) - (caret.getRow() - caret.getRowScrollPosition() + 1) * getLineHeight(), 
            ((cursorColEnd - cursorColStart) * getFont().getSpaceWidth()), 
            getLineHeight()
        );
      } else {
        int rowCount = Math.abs(cursorRowStart - cursorRowEnd) - 1;
        
        shape.rect(sx + gutterWidth() + GUTTER_PADDING + cursorColStart * font.getSpaceWidth(), 
            (sy + height) - (cursorRowStart+ 1 - caret.getRowScrollPosition()) * getLineHeight(), 
            width - gutterWidth() - GUTTER_PADDING - cursorColStart * font.getSpaceWidth(), 
            getLineHeight()
        );
        
        for (int i = 0; i < rowCount; i++) {
          shape.rect(sx + gutterWidth() + GUTTER_PADDING, 
              (sy + height) - (cursorRowStart + i + 2 - caret.getRowScrollPosition()) * getLineHeight(), 
              width - gutterWidth() - GUTTER_PADDING, 
              getLineHeight()
          );
        }
        
        shape.rect(sx + gutterWidth() + GUTTER_PADDING, 
            (sy + height) - (cursorRowEnd + 1 - caret.getRowScrollPosition()) * getLineHeight(), 
            (cursorColEnd - caret.getColScrollPosition()) * font.getSpaceWidth(), 
            getLineHeight()
        );
      }
      
      shape.end();
      
      Gdx.gl.glDisable(GL10.GL_BLEND);
    }
    
    if (focused && !caret.haveSelection()) {
      Gdx.gl.glEnable(GL10.GL_BLEND);
      Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
      shape.begin(ShapeType.Filled);
      shape.setColor(1.0f, 1.0f, 1.0f, 0.1f);
      shape.rect(sx, (sy + height) - (caret.getRow() + 1 - caret.getRowScrollPosition()) * getLineHeight(), width, getLineHeight());
      shape.end();
      Gdx.gl.glDisable(GL10.GL_BLEND);
    }
    
    renderBatch.begin();
    renderBatch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
    
    
    for (int y = 0; y < toLine - fromLine; y++) {
      Line line               = this.lines.get(fromLine + y);
      float linePosY          = (sy + height + font.getDescent()) - y * getLineHeight();
      float lineElementX      = 0;
      
      for (int x = 0; x < line.size(); x++) {
        Element elem      = line.get(x);
        TextBounds bounds = font.getBounds(elem.text);
        font.setColor(styles.get(elem.kind));
        font.draw(renderBatch, elem.text, sx + gutterWidth() + GUTTER_PADDING + lineElementX - xOffset, linePosY );
        
        lineElementX += bounds.width;
        //if (lineElementX > toChar) {
        //  break;
        //}
      }
    }
    if (focused && !disabled) {
      blink();
      if (cursorOn && cursorPatch != null) {
        cursorPatch.draw(renderBatch, sx + gutterWidth() + GUTTER_PADDING + ((caret.getCol()) * getFont().getSpaceWidth()) - xOffset, (sy + height) - (caret.getRow() - caret.getRowScrollPosition() + 1) * getLineHeight(), cursorPatch.getMinWidth(), getLineHeight());
      }
    }

    renderBatch.flush();
    ScissorStack.popScissors();
    
    for (int y = 0; y < toLine - fromLine; y++) {
      String lineNumberString = Integer.toString(fromLine + y+1);
      float linePosY          = (sy + height + font.getDescent()) - y * getLineHeight();
      font.setColor(Color.WHITE);
      font.draw(renderBatch, lineNumberString, sx + gutterWidth() - font.getBounds(lineNumberString).width, linePosY);
    }
    
    font.setColor(Color.WHITE);
    font.draw(renderBatch, "Row "+ caret.getRow() + " Col " + caret.getCol() + " Char " + String.valueOf(caret.getCurrentChar()) + " Scroll Col: " + caret.getColScrollPosition() + " Scroll Row: " + caret.getRowScrollPosition() + " Caret position: "+ caret.getCaretPosition(), sx, sy - getLineHeight() + GUTTER_PADDING);
    scrollbar.setPosition(sx + getWidth(), sy);
    scrollbar.setHeight(height);
    
    //scrollbar.setRange(0, lines.size() - visibleLinesCount());
    int scroll = (int) (scrollbar.getMaxValue() - scrollbar.getValue());
    //caret.setRowScrollPosition(scroll);
   // scrollbar.draw(renderBatch, parentAlpha);
  }
  
  @Override
  public void act(float delta) {
    super.act(delta);
    //scrollbar.act(Gdx.graphics.getDeltaTime());
  }


  private BitmapFont getFont() {
    return ResourceManager.shared().getFont("CURRIER_NEW");
  }

  private void blink() {
    long time = TimeUtils.nanoTime();
    if ((time - lastBlink) / 1000000000.0f > blinkTime) {
      cursorOn = !cursorOn;
      lastBlink = time;
    }
  }

  public void setText(String string) {
    this.text  = string;
    parse(this.text);
  }
  
  public void parse(String text) {
    this.lines.clear();
    JavaScriptScanner js = new JavaScriptScanner(text);
    JavaScriptScanner.Kind kind;
    
    Line line = new Line();
    this.lines.add(line);
    while((kind=js.scan()) != JavaScriptScanner.Kind.EOF) {
      if(kind == JavaScriptScanner.Kind.NEWLINE) {
        line = new Line();
        this.lines.add(line);
      } else {
        line.add(new Element(kind, js.getString()));
      }
    }
    
    for (Line row : lines) {
      row.buildString();
    }
  }
  
  static public class CodeEditorStyle {
    public BitmapFont font;
    public Color fontColor, focusedFontColor, disabledFontColor;
    /** Optional. */
    public Drawable background, focusedBackground, disabledBackground, cursor, selection;
    /** Optional. */
    public BitmapFont messageFont;
    /** Optional. */
    public Color messageFontColor;

    public CodeEditorStyle () {
    }

    public CodeEditorStyle (BitmapFont font, Color fontColor, Drawable cursor, Drawable selection, Drawable background) {
      this.background = background;
      this.cursor = cursor;
      this.font = font;
      this.fontColor = fontColor;
      this.selection = selection;
    }

    public CodeEditorStyle (CodeEditorStyle style) {
      this.messageFont = style.messageFont;
      if (style.messageFontColor != null) this.messageFontColor = new Color(style.messageFontColor);
      this.background = style.background;
      this.focusedBackground = style.focusedBackground;
      this.disabledBackground = style.disabledBackground;
      this.cursor = style.cursor;
      this.font = style.font;
      if (style.fontColor != null) this.fontColor = new Color(style.fontColor);
      if (style.focusedFontColor != null) this.focusedFontColor = new Color(style.focusedFontColor);
      if (style.disabledFontColor != null) this.disabledFontColor = new Color(style.disabledFontColor);
      this.selection = style.selection;
    }
  }

  public void addToStage(Stage stage) {
    stage.addActor(this);
    stage.addActor(this.scrollbar);
  }
  
}