package com.macbury.r0x16.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PsychicsManager {
  private World world;
  private static final float GRAVITY                 = -10.0f;
  public static final float  WORLD_TO_BOX            = 0.10f;
  private static final float BOX_STEP                = 1/60f;
  private static final int   BOX_VELOCITY_ITERATIONS = 8;
  private static final int   BOX_POSITION_ITERATIONS = 3;
  private static final String TAG                    = "PsychicsManager";
  private float psychAccumulator                     = 0.0f;
  private LevelManager level;
  
  public PsychicsManager(LevelManager levelManager) {
    Gdx.app.log(TAG, "Initializing new PsychicsManager");
    this.level    = levelManager;
    world         = new World(new Vector2(0, GRAVITY),true);
  }
  
  public void update(float delta) {
    psychAccumulator += delta;
    while(psychAccumulator > BOX_STEP){
      world.step(BOX_STEP,BOX_VELOCITY_ITERATIONS,BOX_POSITION_ITERATIONS);
      psychAccumulator -= BOX_STEP;
    }
  }
}
