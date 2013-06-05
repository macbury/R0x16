package com.macbury.r0x16.manager;

import java.util.Iterator;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
  private OrthographicCamera boxCamera;
  private World world;
  private static final float GRAVITY                 = -9.0f;
  public static final float  WORLD_TO_BOX            = 0.032f;
  private static final float BOX_STEP                = 1/60f;
  private static final int   BOX_VELOCITY_ITERATIONS = 8;
  private static final int   BOX_POSITION_ITERATIONS = 4;
  private static final String TAG                    = "PsychicsManager";
  private static final int MAX_RAY_COUNT             = 32;
  private float psychAccumulator                     = 0.0f;
  private LevelManager level;
  private RayHandler rayHandler;
  
  public PsychicsManager(LevelManager levelManager) {
    Gdx.app.log(TAG, "Initializing new PsychicsManager");
    this.level    = levelManager;
    world         = new World(new Vector2(0, GRAVITY),true);
    //Gdx.app.log(TAG, "Setting lights FBO: "+Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
    rayHandler    = new RayHandler(world);
    rayHandler.setAmbientLight(0.0f,0.0f,0.0f,0.1f);
    OrthographicCamera camera = level.getCamera();
    boxCamera     = new OrthographicCamera();
    boxCamera.setToOrtho(false, camera.viewportWidth * WORLD_TO_BOX, camera.viewportHeight * WORLD_TO_BOX);
    boxCamera.update();
    updateRayCamera();
  }
  
  private void updateRayCamera() {
    OrthographicCamera camera = level.getCamera();
    boxCamera.position.set(camera.position.x * WORLD_TO_BOX, camera.position.y * WORLD_TO_BOX, camera.position.z);
    boxCamera.update();
    
    rayHandler.setCombinedMatrix(boxCamera.combined, boxCamera.position.x,
        boxCamera.position.y, boxCamera.viewportWidth * boxCamera.zoom,
        boxCamera.viewportHeight * boxCamera.zoom);
    rayHandler.update();
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
    
    updateRayCamera();
  }
  
  public void renderLights() {
    rayHandler.render();
  }
  
  public World getWorld() {
    return world;
  }
  
  public RayHandler getLight() {
    return rayHandler;
  }
}
