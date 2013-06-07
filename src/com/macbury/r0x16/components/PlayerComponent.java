package com.macbury.r0x16.components;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentRenderInterface;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.PsychicsManager;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.utils.Position;

public class PlayerComponent extends Component implements ComponentUpdateInterface {
  final static float MAX_VELOCITY = 7f;
  private static final String TAG = "PlayerComponent";
  boolean jump = false;
  private Fixture playerPhysicsFixture;
  private Fixture playerSensorFixture;
  private Body player;
  private long lastGroundTime;
  private int width  = 32;
  private int height = 64;
  private float playerWeight = 10;
  private float stillTime;
  
  @Override
  public void update(float delta) {
    Vector2 vel = player.getLinearVelocity();
    Vector2 pos = player.getPosition();
    
    boolean grounded = true;//isPlayerGrounded(delta);
    
    if(grounded) {
      lastGroundTime = System.nanoTime();
    } else {
      if(System.nanoTime() - lastGroundTime < 100000000) {
        grounded = true;
      }
    }
    
    if(Math.abs(vel.x) > MAX_VELOCITY) {      
      vel.x = Math.signum(vel.x) * MAX_VELOCITY;
      player.setLinearVelocity(vel.x, vel.y);
    }
    
    if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {      
      stillTime += Gdx.graphics.getDeltaTime();
      player.setLinearVelocity(vel.x * 0.9f, vel.y);
    } else { 
      stillTime = 0;
    }
    
    //if(!grounded) {     
   //   playerPhysicsFixture.setFriction(0f);
     // playerSensorFixture.setFriction(0f);      
    //} else {
      /*if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
        playerPhysicsFixture.setFriction(100f);
        playerSensorFixture.setFriction(100f);
      } else {
        playerPhysicsFixture.setFriction(0.2f);
        playerSensorFixture.setFriction(0.2f);
      }*/
   // }
    
      // apply left impulse, but only if max velocity is not reached yet
    if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
      player.applyLinearImpulse(-20f, 0, pos.x, pos.y, true);
    }
 
    // apply right impulse, but only if max velocity is not reached yet
    if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
      player.applyLinearImpulse(20f, 0, pos.x, pos.y, true);
    }
    
    player.setAwake(true);
  }

  private boolean isPlayerGrounded(float deltaTime) {    
    List<Contact> contactList = getOwner().getLevel().getPsychicsManager().getWorld().getContactList();
    for(int i = 0; i < contactList.size(); i++) {
      Contact contact = contactList.get(i);
      if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
         contact.getFixtureB() == playerSensorFixture)) {       
 
        Vector2 pos = player.getPosition();
        WorldManifold manifold = contact.getWorldManifold();
        boolean below = true;
        for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
          below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
        }
 
        if(below) {
          if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("p")) {
            //groundedPlatform = (MovingPlatform)contact.getFixtureA().getBody().getUserData();             
          }
 
          if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("p")) {
           // groundedPlatform = (MovingPlatform)contact.getFixtureB().getBody().getUserData();
          }                     
          return true;      
        }
 
        return false;
      }
    }
    return false;
  }

  @Override
  public void setup() {
    Entity e = getOwner();
    e.setWidth(width);
    e.setHeight(height);
    BodyDef def = new BodyDef();
    def.type    = BodyType.DynamicBody;
    def.position.set(e.getCenterPositionX() * PsychicsManager.WORLD_TO_BOX, e.getCenterPositionY() * PsychicsManager.WORLD_TO_BOX);
    
    Body box                   = e.getLevel().getPsychicsManager().getWorld().createBody(def);
    PolygonShape poly          = new PolygonShape();   
    poly.setAsBox(e.getWidth() * PsychicsManager.WORLD_TO_BOX / 2, e.getHeight() * PsychicsManager.WORLD_TO_BOX / 2);
    
    FixtureDef fixDef          = new FixtureDef();
    fixDef.shape               = poly;
    fixDef.density             = playerWeight;
    fixDef.filter.categoryBits = PsychicsManager.FILTER_CATEGORY_PLAYER;
    playerPhysicsFixture       = box.createFixture(fixDef);
    poly.dispose();     
 
    CircleShape circle         = new CircleShape();   
    circle.setRadius(e.getWidth() / 2 * PsychicsManager.WORLD_TO_BOX);
    circle.setPosition(new Vector2(0, -e.getHeight() / 2 * PsychicsManager.WORLD_TO_BOX));
    
    fixDef                     = new FixtureDef();
    fixDef.shape               = circle;
    fixDef.density             = 0.0f;
    fixDef.filter.categoryBits = PsychicsManager.FILTER_CATEGORY_PLAYER;
    playerSensorFixture        = box.createFixture(fixDef);   
    circle.dispose();
 
    box.setBullet(true);
    
    player = box;
    player.setUserData(e);
    player.setFixedRotation(true);
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void configure(Map<String, String> map) {
    this.width        = Integer.parseInt(map.get("width"));
    this.height       = Integer.parseInt(map.get("height"));
    this.playerWeight = Float.parseFloat(map.get("weight"));
  }



}
