package com.macbury.r0x16.manager;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.macbury.r0x16.entities.Entity;

public class EntityManager extends ArrayList<Entity> {
  private static final long serialVersionUID = 1L;
  private static final String TAG = "EntityManager";
  private LevelManager level;
  private int renderCount = 0;
  private int updateCount = 0;
  private SpriteBatch entityBatch;
  
  public EntityManager(LevelManager levelManager) {
    Gdx.app.log(TAG, "Initializing new EntityManager with max ");
    this.level       = levelManager;
    this.entityBatch = new SpriteBatch();
  }

  public Entity build() {
    Entity e = new Entity();
    e.setLevel(level);
    this.add(e);
    return e;
  }
  
  public void render() {
    OrthographicCamera camera = level.getCamera();
    entityBatch.setProjectionMatrix(camera.combined);
    entityBatch.begin();
    renderCount               = 0;
    for (int i = 0; i < this.size(); i++) {
      Entity e = this.get(i);
      if (camera.frustum.pointInFrustum(e.getPosition())) {
        e.render(entityBatch);
        renderCount++;
      }
    }
    entityBatch.end();
  }
  
  public void update(float delta) {
    updateCount = 0;
    for (int i = 0; i < this.size(); i++) {
      Entity e = this.get(i);
      e.update(delta);
      updateCount++;
    }
  }
  
  public int getRenderCounts() {
    return renderCount;
  }
  
  public int getUpdateCounts() {
    return updateCount;
  }
}
