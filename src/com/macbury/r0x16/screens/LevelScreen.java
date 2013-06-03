package com.macbury.r0x16.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.macbury.r0x16.Core;
import com.macbury.r0x16.components.SpriteComponent;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.LevelManager;
import com.macbury.r0x16.manager.ResourceManager;

public class LevelScreen implements Screen {
  SpriteBatch debugBatch;
  LevelManager level;
  
  public LevelScreen() {
    level      = new LevelManager("test.level");
    Entity e   = level.getEntityManager().build();
    
    TextureAtlas atlas = ResourceManager.shared().getAtlas("DEVELOPER_ATLAS");
    SpriteComponent spriteComponent = (SpriteComponent)e.addComponent(SpriteComponent.class);
    spriteComponent.setSprite(atlas.findRegion("devTrans"));
    e.getPosition().x = 1;
    debugBatch = new SpriteBatch();
  }
  
  @Override
  public void render(float delta) {
    level.render();
    
    if (Core.DEBUG) {
      renderDebug();
    }
    
    level.update(delta);
  }

  private void renderDebug() {
    debugBatch.setProjectionMatrix(level.getCamera().combined);
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
