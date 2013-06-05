package com.macbury.r0x16.screens;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

// https://code.google.com/p/box2dlights/
// http://obviam.net/index.php/getting-started-in-android-game-development-with-libgdx-create-a-working-prototype-in-a-day-tutorial-part-1/
public class LightTestScreen implements Screen {
  
  private static final int RAYS_PER_BALL = 64;
  private World world;
  private RayHandler rayHandler;
  private OrthographicCamera camera;
  
  public LightTestScreen() {
    camera = new OrthographicCamera(48, 32);
    camera.position.set(0, 16, 0);
    camera.update();
    
    world = new World(new Vector2(0, -10), true);
    
    
    //RayHandler.setColorPrecisionMediump();
    RayHandler.setGammaCorrection(true);
    rayHandler = new RayHandler(world);
    rayHandler.setAmbientLight(0.0f);
    rayHandler.setCulling(true);
    rayHandler.setBlur(true);
    rayHandler.setBlurNum(1);
    rayHandler.setShadows(true);
    rayHandler.setAmbientLight(0.6f,0.6f,0.6f,1f);
    
    camera.update(true);
    
    CircleShape ballShape = new CircleShape();
    ballShape.setRadius(2);
    
    FixtureDef def = new FixtureDef();
    def.restitution = 0.9f;
    def.friction = 0.01f;
    def.shape = ballShape;
    def.density = 1f;
    
    BodyDef boxBodyDef = new BodyDef();
    boxBodyDef.type    = BodyType.DynamicBody;
    
    boxBodyDef.position.x = -20 + (float) (Math.random() * 40);
    boxBodyDef.position.y = 10 + (float) (Math.random() * 15);
    Body boxBody = world.createBody(boxBodyDef);
    boxBody.createFixture(def);
    
    Light light = new PointLight(rayHandler, 32);
    light.attachToBody(boxBody, 0, 0);
  }
  
  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    camera.update();
    
    rayHandler.update();
    rayHandler.setCombinedMatrix(camera.combined, camera.position.x,
        camera.position.y, camera.viewportWidth * camera.zoom,
        camera.viewportHeight * camera.zoom);
    rayHandler.render();
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
