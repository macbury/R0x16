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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.macbury.r0x16.entities.Entity;
//http://hsapkota.com.au/index.php/blog/eclipse/29-tutorial-embed-lua-in-java-w-libgdx-and-box2d
//http://rotatingcanvas.com/using-box2d-in-libgdx-game-part-iii/  
//https://code.google.com/p/libgdx/wiki/PhysicsBox2D
//http://www.emanueleferonato.com/2012/05/16/simulating-mudslime-with-box2d-bitmaps-and-filters/
//http://www.badlogicgames.com/wordpress/?p=2017
//http://obviam.net/index.php/getting-started-in-android-game-development-with-libgdx-tutorial-part-4-collision-detection/
public class PsychicsManager {
  public final static short FILTER_CATEGORY_SCENERY  = 0x0001;
  public final static short FILTER_CATEGORY_LIGHT    = 0x0002; //LIGHT PASS THROUGH
  public static final short FILTER_CATEGORY_PLAYER   = 0x0004;
  public final static short FILTER_MASK_SCENERY      = -1;
  public final static short FILTER_MASK_PLAYER       = FILTER_CATEGORY_SCENERY | FILTER_CATEGORY_LIGHT;
  public final static short FILTER_MASK_LIGHT        = FILTER_CATEGORY_SCENERY;
  
  private OrthographicCamera boxCamera;
  private World world;
  private static final float GRAVITY                 = -9.8f;
  public static final float  WORLD_TO_BOX            = 0.032f;
  private static final float BOX_STEP                = 1/60f;
  private static final int   BOX_VELOCITY_ITERATIONS = 8;
  private static final int   BOX_POSITION_ITERATIONS = 4;
  private static final String TAG                    = "PsychicsManager";
  private static final int MAX_RAY_COUNT             = 32;
  
  private float psychAccumulator                     = 0.0f;
  private LevelManager level;
  private RayHandler rayHandler;
  private Box2DDebugRenderer debugRender;
  private boolean enabled = true;
  public PsychicsManager(LevelManager levelManager) {
    Gdx.app.log(TAG, "Initializing new PsychicsManager");
    
    debugRender   = new Box2DDebugRenderer();
    
    this.level    = levelManager;
    world         = new World(new Vector2(0, GRAVITY),true);
    //Gdx.app.log(TAG, "Setting lights FBO: "+Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
    rayHandler    = new RayHandler(world);
    rayHandler.setAmbientLight(0.0f,0.0f,0.0f,0.3f);
    OrthographicCamera camera = level.getCamera();
    boxCamera     = new OrthographicCamera();
    boxCamera.setToOrtho(false, camera.viewportWidth * WORLD_TO_BOX, camera.viewportHeight * WORLD_TO_BOX);
    boxCamera.update();
    updateRayCamera();
  }
  
  private void updateRayCamera() {
    OrthographicCamera camera = level.getCamera();
    boxCamera.setToOrtho(false, camera.viewportWidth * WORLD_TO_BOX, camera.viewportHeight * WORLD_TO_BOX);
    boxCamera.position.set(camera.position.x * WORLD_TO_BOX, camera.position.y * WORLD_TO_BOX, camera.position.z);
    boxCamera.update();
    
    rayHandler.setCombinedMatrix(boxCamera.combined, boxCamera.position.x,
        boxCamera.position.y, boxCamera.viewportWidth * boxCamera.zoom,
        boxCamera.viewportHeight * boxCamera.zoom);
    rayHandler.update();
  }
  
  public static void updateEntityByBody(Body b) {
    Entity e = (Entity) b.getUserData();
    
    if (e != null) {
      e.setCenterPosition(b.getPosition().x / PsychicsManager.WORLD_TO_BOX, b.getPosition().y / PsychicsManager.WORLD_TO_BOX);
      e.setRotation(MathUtils.radiansToDegrees * b.getAngle());
    }
  }
  
  public void update(float delta) {
    if (enabled) {
      psychAccumulator += delta;
      while(psychAccumulator > BOX_STEP){
        world.step(BOX_STEP,BOX_VELOCITY_ITERATIONS,BOX_POSITION_ITERATIONS);
        psychAccumulator -= BOX_STEP;
      }
      
      Iterator<Body> bi = world.getBodies();
      
      while (bi.hasNext()){
        PsychicsManager.updateEntityByBody(bi.next());
      }
    }
    
  }
  
  
  public World getWorld() {
    return world;
  }
  
  public RayHandler getLight() {
    return rayHandler;
  }

  public void render() {
    updateRayCamera();
    rayHandler.render();
  }
  
  public void renderDebug() {
    debugRender.render( getWorld(), boxCamera.combined );
  }

  public void pause() {
    this.enabled = false;
  }
  
  // sync box 2d bodies with entities positon
  public void syncWithEntites() {
    Iterator<Body> bi = world.getBodies();
    
    while (bi.hasNext()){
      PsychicsManager.updateBodyByEntity(bi.next());
    }
  }

  private static void updateBodyByEntity(Body b) {
    Entity e = (Entity) b.getUserData();
    
    if (e != null) {
      b.setTransform(e.getCenteredPositionInMeters().x, e.getCenteredPositionInMeters().y, e.getRotation() / MathUtils.radiansToDegrees);
    }
  }
}
