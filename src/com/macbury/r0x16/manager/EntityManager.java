package com.macbury.r0x16.manager;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.macbury.r0x16.entities.Entity;

public class EntityManager extends ArrayList<Entity> {
  private static final String TAG = "EntityManager";
  private LevelManager level;
  
  public EntityManager(LevelManager levelManager) {
    Gdx.app.log(TAG, "Initializing new EntityManager with max ");
    this.level = levelManager;
  }

  public void render() {
    
  }
  
  public void update(float delta) {
    
  }
}
