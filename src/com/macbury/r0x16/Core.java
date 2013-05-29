package com.macbury.r0x16;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.screens.CodeEditorTest;
import com.macbury.r0x16.screens.LightTestScreen;

public class Core extends Game {
  static Core _shared;
  
  @Override
  public void create() {
    try {
      ResourceManager.shared().load();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    setScreen(new CodeEditorTest());
  }

  public static ApplicationListener shared() {
    if (_shared == null) {
      _shared = new Core();
    }
    return _shared;
  }

}
