package com.macbury.r0x16.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.macbury.r0x16.manager.PsychicsManager;

public class Position extends Vector3 {
  private static final long serialVersionUID = 1L;
  
  public float getXInMeters() {
    return this.x * PsychicsManager.WORLD_TO_BOX;
  }
  
  public float getYInMeters() {
    return this.y * PsychicsManager.WORLD_TO_BOX;
  }
  
  public float setXInMeters(float x) {
    return this.x = x / PsychicsManager.WORLD_TO_BOX;
  }
  
  public float setYInMeters(float y) {
    return this.y = y / PsychicsManager.WORLD_TO_BOX;
  }
}
