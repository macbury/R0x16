package com.macbury.r0x16.screens;

import com.badlogic.gdx.Screen;
import com.macbury.r0x16.twl.BaseInterface;

import de.matthiasmann.twl.Button;

public class TwlTest implements Screen {
  BaseInterface inte;
  public TwlTest() {
    inte  = new BaseInterface();
  }
  
  @Override
  public void render(float delta) {
    inte.update();
  }

  @Override
  public void resize(int width, int height) {
    // TODO Auto-generated method stub

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
