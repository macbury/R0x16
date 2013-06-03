package com.macbury.r0x16.screens;

import com.badlogic.gdx.Screen;
import com.macbury.r0x16.manager.LevelManager;

public class LevelScreen implements Screen {
  
  LevelManager level;
  
  public LevelScreen() {
    level = new LevelManager("test.level");
  }
  
  @Override
  public void render(float delta) {
    level.render();
    level.update(delta);
  }

  @Override
  public void resize(int width, int height) {
    
  }

  @Override
  public void show() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub

  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

}
