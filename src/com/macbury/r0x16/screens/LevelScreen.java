package com.macbury.r0x16.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.macbury.r0x16.Core;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.LevelManager;
import com.macbury.r0x16.manager.ResourceManager;

public class LevelScreen implements Screen {
  SpriteBatch debugBatch;
  LevelManager level;
  
  public LevelScreen() {
    level      = new LevelManager();
    for (int i = 0; i < 20; i++) {
      Entity e   = level.getEntityManager().build("GROUND");
      e.getPosition().x = 64 * i;
      e.getPosition().y = 0;
    }
    
    for (int i = 0; i < 20; i++) {
      Entity e   = level.getEntityManager().build("GROUND");
      e.getPosition().x = 0;
      e.getPosition().y = i * 64;
    }
    
    for (int i = 0; i < 20; i++) {
      Entity e   = level.getEntityManager().build("GROUND");
      e.setPosition(1280, i * 64);
    }
    
    for (int i = 8; i < 10; i++) {
      Entity e   = level.getEntityManager().build("CUBE");
      e.setPosition(i * 64, 700);
    }
    
    for (int i = 8; i < 12; i++) {
      Entity e   = level.getEntityManager().build("CUBE");
      e.setPosition(i * 64, 764);
    }
    
    for (int i = 12; i < 13; i++) {
      Entity e   = level.getEntityManager().build("BOUNCY_CUBE");
      e.setPosition(i * 64 - 53, 500);
    }
    
    for (int i = 8; i < 14; i++) {
      Entity e   = level.getEntityManager().build("LIGHT_BALL");
      e.getPosition().x = i * 68;
      e.getPosition().y = 600;
    }
    
    Entity e   = level.getEntityManager().build("PLAYER");
    e.getPosition().x = 400;
    e.getPosition().y = 64;
    debugBatch = new SpriteBatch();
  }
  
  @Override
  public void render(float delta) {
    level.update(delta);
    level.render();
    level.getPsychicsManager().renderDebug();
    if (Core.DEBUG) {
      renderDebug();
    }
  }

  private void renderDebug() {
    //debugBatch.setProjectionMatrix(level.getCamera().combined);
    debugBatch.begin();
    ResourceManager.shared().getFont("CURRIER_NEW").draw(debugBatch, "FPS: "+ Gdx.graphics.getFramesPerSecond() + " Renders: "+level.getEntityManager().getRenderCounts() + " Updates: " + level.getEntityManager().getUpdateCounts() + " Entities: " + level.getEntityManager().size(), 10, 20);
    debugBatch.end();
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
