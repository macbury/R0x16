package com.macbury.r0x16.widgets.code_editor;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.widgets.JavaScriptScanner;
import com.macbury.r0x16.widgets.JavaScriptScanner.Kind;

public class CodeEditor extends Widget {
  ShapeRenderer shape;
  CodeEditorStyle style;
  private final static int GUTTER_PADDING = 10;
  private static final String TAG = "CodeEditor";
  private final Rectangle fieldBounds = new Rectangle();
  private final TextBounds textBounds = new TextBounds();
  private ArrayList<Line> lines;
  boolean disabled;
  private String text = "";
  private int lineY = 0;
  private HashMap<JavaScriptScanner.Kind, Color> styles;
  public CodeEditor(Skin skin) {
    style  = skin.get(CodeEditorStyle.class);
    lines  = new ArrayList<Line>();
    styles = new HashMap<JavaScriptScanner.Kind, Color>();
    styles.put(JavaScriptScanner.Kind.KEYWORD, new Color(205.0f/255.0f, 168.0f/255.0f, 105.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.NORMAL, Color.WHITE);
    styles.put(JavaScriptScanner.Kind.STRING, new Color(143.0f/255.0f, 157.0f/255.0f, 106.0f/255.0f, 1.0f));
    styles.put(JavaScriptScanner.Kind.COMMENT, new Color(95.0f/255.0f, 90.0f/255.0f, 96.0f/255.0f, 1.0f));
    shape = new ShapeRenderer();
  }
  
  public float getLineHeight() {
    return style.font.getLineHeight();
  }
  
  private int gutterWidth() {
    return 40;
  }
  
  private int visibleLinesCount() {
    return (int) (this.getHeight() / getLineHeight());
  }
  
  @Override
  public void draw(SpriteBatch renderBatch, float parentAlpha) {
    
    
    Stage stage     = getStage();
    boolean focused = stage != null && stage.getKeyboardFocus() == this;
    
    final BitmapFont font = ResourceManager.shared().getFont("CURRIER_NEW");
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
    
    renderBatch.begin();
    renderBatch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
    
    int fromLine = lineY;
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
        Gdx.app.log(TAG, kind.toString());
        line = new Line();
        this.lines.add(line);
      } else {
        line.add(new Element(kind, js.getString()));
        Gdx.app.log(TAG, js.getString());
      }
    }
    
  }
}
