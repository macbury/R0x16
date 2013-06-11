package com.macbury.r0x16;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
 
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.macbury.r0x16.manager.PrefabManager;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.screens.CodeEditorTest;
import com.macbury.r0x16.screens.LevelScreen;
import com.macbury.r0x16.screens.LightTestScreen;

public class Core extends Game {
  public static boolean DEBUG = true;
  public final static String CURSOR_NORMAL = "CURSOR_ARROW";
  public final static String CURSOR_TEXT   = "CURSOR_SELECT";
  static Core _shared;
  static org.lwjgl.input.Cursor emptyCursor;
  SpriteBatch cursorBatch;
  OrthographicCamera camera;
  int xHotspot, yHotspot;
  
  boolean hwVisible = false;
  private String currentCursor = CURSOR_NORMAL;
  
  @Override
  public void create() {
    try {
      ResourceManager.shared().load();
      PrefabManager.shared().load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    cursorBatch = new SpriteBatch();
    camera      = new OrthographicCamera();
    xHotspot    = 5;
    yHotspot    = 32;
    setScreen(new LevelScreen());
  }

  public static Core shared() {
    if (_shared == null) {
      _shared = new Core();
    }
    return _shared;
  }

  public static void setHWCursorVisible(boolean visible) throws LWJGLException {
    if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication)
      return;
    if (emptyCursor == null) {
      if (Mouse.isCreated()) {
        int min = org.lwjgl.input.Cursor.getMinCursorSize();
        IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
        emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
      } else {
        throw new LWJGLException(
            "Could not create empty cursor before Mouse object is created");
      }
    }
    if (Mouse.isInsideWindow())
      Mouse.setNativeCursor(visible ? null : emptyCursor);
  }

  @Override
  public void render() {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    
    super.render();
    renderCursor();
  }

  private void renderCursor() {
    try {
      setHWCursorVisible(hwVisible);
    } catch (LWJGLException e) {
      throw new GdxRuntimeException(e);
    }
    camera.update();
    cursorBatch.setProjectionMatrix(camera.combined);
    cursorBatch.begin();
    int x = Gdx.input.getX();
    int y = Gdx.input.getY();
    cursorBatch.draw(ResourceManager.shared().getTexture(getCurrentCursor()), x - xHotspot, Gdx.graphics.getHeight() - y - 1 - yHotspot);
    cursorBatch.end();
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    camera.setToOrtho(false, width, height);
    cursorBatch.setProjectionMatrix(camera.combined);
  }

  public String getCurrentCursor() {
    return currentCursor;
  }

  public void setCurrentCursor(String currentCursor) {
    this.currentCursor = currentCursor;
  }
  
  
}
