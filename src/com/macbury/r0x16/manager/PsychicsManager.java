package com.macbury.r0x16.manager;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.macbury.r0x16.entities.Entity;
//http://hsapkota.com.au/index.php/blog/eclipse/29-tutorial-embed-lua-in-java-w-libgdx-and-box2d
//http://rotatingcanvas.com/using-box2d-in-libgdx-game-part-iii/  
//https://code.google.com/p/libgdx/wiki/PhysicsBox2D
//http://www.emanueleferonato.com/2012/05/16/simulating-mudslime-with-box2d-bitmaps-and-filters/
//http://www.badlogicgames.com/wordpress/?p=2017
public class PsychicsManager {
  private World world;
  private static final float GRAVITY                 = -9.0f;
  public static final float  WORLD_TO_BOX            = 0.032f;
  private static final float BOX_STEP                = 1/60f;
  private static final int   BOX_VELOCITY_ITERATIONS = 8;
  private static final int   BOX_POSITION_ITERATIONS = 4;
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
    
    Iterator<Body> bi = world.getBodies();
    
    while (bi.hasNext()){
      Body b = bi.next();
      Entity e = (Entity) b.getUserData();

      if (e != null) {
        e.setCenterPosition(b.getPosition().x / PsychicsManager.WORLD_TO_BOX, b.getPosition().y / PsychicsManager.WORLD_TO_BOX);
        e.setRotation(MathUtils.radiansToDegrees * b.getAngle());
      }
    }
  }

  public World getWorld() {
    return world;
  }
}
