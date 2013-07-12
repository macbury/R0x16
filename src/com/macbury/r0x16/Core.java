package com.macbury.r0x16;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.graphics.GL10;
import com.macbury.r0x16.game_editor.LevelEditorFrame;
import com.macbury.r0x16.manager.PrefabManager;
import com.macbury.r0x16.manager.ResourceManager;

public class Core extends Game {
  public static LwjglFrame frame;
  public static boolean DEBUG              = true;
  private static final String TAG          = "Core";
  static Core _shared;

  
  boolean hwVisible            = false;
  private LwjglApplicationConfiguration config;
  private List<String> argsList;
  

  public static Core shared() {
    if (_shared == null) {
      _shared = new Core();
    }
    return _shared;
  }


  @Override
  public void render() {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    
    super.render();
  }

  

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
  }


  public void bootstrap(String[] args) {
    this.config = new LwjglApplicationConfiguration();
    config.title     = "R0x16";
    config.useGL20   = true;
    config.width     = 1366;
    config.height    = 768;
    config.resizable = false;
    config.samples   = 0;
    config.vSyncEnabled = true;
    config.fullscreen = false;
    
    this.argsList = Arrays.asList(args);
    if (argsList.contains("--editor")) {
      bootAsEditor(config);
    } else {
      bootAsGame(config);
      //Core.frame = new LwjglFrame(Core.shared(), config);
    }
    //Core.frame.setResizable(false);
    //Core.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    //Core.frame.addWindowListener(new WindowEventHandler());
    //Core.frame.setCursor(Cursor.getPredefinedCursor(Cursor.));
    
  }
  
  private void bootAsEditor(LwjglApplicationConfiguration config) {
    LevelEditorFrame app = new LevelEditorFrame(config);
    app.setVisible(true);
  }

  private void bootAsGame(LwjglApplicationConfiguration config) {
    LwjglApplication app = new LwjglApplication(Core.shared(), config);
  }

  class WindowEventHandler extends WindowAdapter {
    public void windowClosing(WindowEvent evt) {
      Gdx.app.exit();
      Core.frame.dispose();
    }
  }

  @Override
  public void create() {
    try {
      ResourceManager.shared().load();
      PrefabManager.shared().load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
}
