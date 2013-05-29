package com.macbury.r0x16.widgets.code_editor;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Mouse;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
  ShapeRenderer shape;
  CodeEditorStyle style;
  private final static int GUTTER_PADDING = 10;
  private static final String TAG = "CodeEditor";
  private static final float LINE_PADDING = 2;
  private final Rectangle fieldBounds = new Rectangle();
  private final TextBounds textBounds = new TextBounds();
  private ArrayList<Line> lines;
  boolean disabled;
  private String text           = "";
  private int rowScrollPosition = 0;
  private int row               = 0;
  private int col               = 0;
  private float blinkTime       = 0.32f;
  private int selectionStartRow = -1;
  private int selectionStartCol = -1;
  private int selectionEndRow   = -1;
  private int selectionEndCol   = -1;
  
  private HashMap<JavaScriptScanner.Kind, Color> styles;
  
  private long lastBlink;
  private boolean cursorOn;
  private Clipboard clipboard;
  private ClickListener inputListener;
  private boolean hasSelection;
  public CodeEditor(Skin skin) {
    style  = skin.get(CodeEditorStyle.class);
    lines  = new ArrayList<Line>();
    styles = new HashMap<JavaScriptScanner.Kind, Color>();
    styles.put(JavaScriptScanner.Kind.KEYWORD, new Color(252.0f/255.0f, 128.0f/255.0f, 58.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.NORMAL, Color.WHITE);
    styles.put(JavaScriptScanner.Kind.STRING, new Color(142.0f/255.0f, 198.0f/255.0f, 95.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.COMMENT, new Color(95.0f/255.0f, 90.0f/255.0f, 96.0f/255.0f, 1.0f));
    shape = new ShapeRenderer();
    
    this.clipboard = Gdx.app.getClipboard();
    setWidth(getPrefWidth());
    setHeight(getPrefHeight());
    
    initializeKeyboard();
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
        clearSelection();
        setCursorPosition(xToCol(x), yToRow(y));
        //selectionStart = cursor;
        Stage stage = getStage();
        if (stage != null) stage.setKeyboardFocus(CodeEditor.this);
        Core.shared().setCurrentCursor(Core.CURSOR_TEXT);
        return true;
      }
      
      public boolean keyDown(InputEvent event, int keycode) {
        return onKeyDown(event, keycode);
      }
    });
  }
  
  protected boolean onKeyDown(InputEvent event, int keycode) {
    if (disabled) return false;
    
    final BitmapFont font = getFont();

    lastBlink = 0;
    cursorOn = false;
    Stage stage = getStage();
    if (stage != null && stage.getKeyboardFocus() == this) {
      boolean repeat = false;
      
      if (keycode == Keys.UP && row > 0) {
        row -= 1;
        fixColBoundsValue();
      }
      
      if (keycode == Keys.DOWN && row < this.lines.size() - 1) {
        row += 1;
        fixColBoundsValue();
      }
      
      if (keycode == Keys.HOME) {
        col = 0;
      }
      
      if (keycode == Keys.END) {
        Line line = getCurrentLine();
        if (line != null) {
          col = line.textLenght();
        } else {
          col = 0;
        }
        clearSelection();
      }
      
      return true;
    } else {
      return false;
    }
  }
  
  private Line getCurrentLine() {
    return getLineForRow(row);
  }

  private void delete() {
    // TODO Auto-generated method stub
    
  }

  private void cut() {
    // TODO Auto-generated method stub
    
  }

  private void copy() {
    // TODO Auto-generated method stub
    
  }

  private void paste() {
    // TODO Auto-generated method stub
    
  }

  protected int xToCol(float x) {
    int c = (int) Math.floor((x - gutterWidth() - GUTTER_PADDING) / getFont().getSpaceWidth());
    if (c < 0) {
      c = 0;
    }
    return c;
  }
  
  protected int yToRow(float y) {
    int r = (int) Math.floor((getHeight() - y) / getLineHeight());
    if (r < 0) {
      r = 0;
    }
    return r;
  }
  
  protected void setCursorPosition(int x, int y) {
    Gdx.app.log(TAG, "Set cursor position at: " + x + "x"+y);
    Line line = getLineForRow(y);
    
    if (line == null) {
      row = (this.lines.size() - 1);
      line = this.getLineForRow(row);
    } else {
      row = y;
    }
    
    col = x;
    fixColBoundsValue();
  }

  private void fixColBoundsValue() {
    Line line = getCurrentLine();
    if (col > line.textLenght()) {
      col = line.textLenght();
    }
  }

  private Line getLineForRow(int y) {
    try {
      return lines.get(y);
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  protected void clearSelection() {
    
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
  
  private int getCaretPositionFor(int r, int c) {
    Line line = getLineForRow(r);
    if (line == null) {
      return 0;
    } else {
      int width = c;
      for (int y = 0; y < r; y++) {
        line = getLineForRow(y);
        width += line.textLenght();
      }
      return width;
    }
  }
  
  private int getCaretPosition() {
    return getCaretPositionFor(row, col);
  }
  
  @Override
  public void draw(SpriteBatch renderBatch, float parentAlpha) {
    
    
    Stage stage     = getStage();
    boolean focused = stage != null && stage.getKeyboardFocus() == this;
    
    final BitmapFont font = getFont();
    final Drawable selection = style.selection;
    final Drawable cursorPatch = style.cursor;

    Color color = getColor();
    float sx = getX();
    float sy = getY();
    float width = getWidth();
    float height = getHeight();
    
    renderBatch.end();
    shape.setProjectionMatrix(renderBatch.getProjectionMatrix());
    shape.begin(ShapeType.FilledRectangle);
    shape.setColor(0.1f, 0.1f, 0.1f, 1);
    
    shape.filledRect(sx, sy, width, height);
    shape.end();
    
    shape.begin(ShapeType.FilledRectangle);
    shape.setColor(0.25f, 0.25f, 0.25f, 1.0f);
    shape.filledRect(sx, sy, gutterWidth() + GUTTER_PADDING / 2 , height);
    shape.end();
    
    if (focused) {
      Gdx.gl.glEnable(GL10.GL_BLEND);
      Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
      shape.begin(ShapeType.FilledRectangle);
      shape.setColor(1.0f, 1.0f, 1.0f, 0.1f);
      shape.filledRect(sx, (sy + height) - (row + 1) * getLineHeight(), width, getLineHeight());
      shape.end();
      Gdx.gl.glDisable(GL10.GL_BLEND);
    }
    
    renderBatch.begin();
    renderBatch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
    
    int fromLine = rowScrollPosition;
    int toLine   = Math.min(fromLine + visibleLinesCount(), this.lines.size());
    
    for (int y = fromLine; y < toLine; y++) {
      Line line               = this.lines.get(y);
      float linePosY          = (sy + height + font.getDescent()) - y * getLineHeight();
      float lineElementX      = 0;
      
      String lineNumberString = Integer.toString(y+1);
      font.setColor(Color.WHITE);
      font.draw(renderBatch, lineNumberString, sx + gutterWidth() - font.getBounds(lineNumberString).width, linePosY);
      
      for (int x = 0; x < line.size(); x++) {
        Element elem      = line.get(x);
        TextBounds bounds = font.getBounds(elem.text);
        font.setColor(styles.get(elem.kind));
        font.draw(renderBatch, elem.text, sx + gutterWidth() + GUTTER_PADDING + lineElementX, linePosY );
        
        lineElementX += bounds.width;
      }
    }
    
    if (focused && !disabled) {
      blink();
      if (cursorOn && cursorPatch != null) {
        cursorPatch.draw(renderBatch, sx + gutterWidth() + GUTTER_PADDING + (col * getFont().getSpaceWidth()), (sy + height) - (row + 1) * getLineHeight(), cursorPatch.getMinWidth(), getLineHeight());
      }
    }
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
        //Gdx.app.log(TAG, kind.toString());
        line = new Line();
        this.lines.add(line);
      } else {
        line.add(new Element(kind, js.getString()));
        //Gdx.app.log(TAG, js.getString());
      }
    }
    
  }
}