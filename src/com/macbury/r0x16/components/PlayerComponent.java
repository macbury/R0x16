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

public class PlayerComponent extends Component implements ComponentUpdateInterface, ComponentRenderInterface {
  final static float MAX_VELOCITY      = 6f;
  private float MAX_FALL_VELOCITY      = 20f;
  private static final String TAG      = "PlayerComponent";
  private static final float MAX_SLOPE = 3000;
  boolean jump = false;
  private Fixture playerPhysicsFixture;
  private Fixture playerSensorFixture;
  private Body player;
  private long lastGroundTime;
  private int width  = 32;
  private int height = 64;
  private float playerWeight = 10;
  private float stillTime;
  private float moveSpeed = 4.0f;
  private float jumpPower = 20.0f;
  private boolean grounded;
  private float sensorHeight;
  private float slopeFactor;
  private float sensorPositionY = -0.6f;
  
  private State state = State.Idle;
  private AnimatedSpriteComponent playerAnimation; 
  
  public enum State {
    Idle, Walking, Jumping
  }
  
  @Override
  public void update(float delta) {
    Vector2 vel = player.getLinearVelocity();
    Vector2 pos = player.getPosition();
    
    this.grounded    = isPlayerGrounded(delta);
    this.jump        = Gdx.input.isKeyPressed(Keys.W);
    boolean keyLeft  = Gdx.input.isKeyPressed(Keys.A);
    boolean keyRight = Gdx.input.isKeyPressed(Keys.D);
    if(grounded) {
      lastGroundTime = System.nanoTime();
    } else {
      if(System.nanoTime() - lastGroundTime < 100000000) {
        grounded = true;
      }
    }
    
    if(!keyLeft && !keyRight) {      
      stillTime += Gdx.graphics.getDeltaTime();
      player.setLinearVelocity(vel.x * 0.1f, vel.y);
    } else { 
      stillTime = 0;
    }
    
    if(!grounded) {     
      playerPhysicsFixture.setFriction(0f);
      playerSensorFixture.setFriction(0f);      
    } else {
      if(!keyLeft && !keyRight) {
        playerPhysicsFixture.setFriction(100f);
        playerSensorFixture.setFriction(100f);
      } else {
        playerPhysicsFixture.setFriction(0.2f);
        playerSensorFixture.setFriction(0.2f);
      }
    }
    
    float speed = moveSpeed;
    
    if(keyLeft && vel.x > -MAX_VELOCITY) {
      player.applyLinearImpulse(-speed, 0, pos.x, pos.y, true);
    }
    
    if(keyRight && vel.x < MAX_VELOCITY) {
      player.applyLinearImpulse(speed, 0, pos.x, pos.y, true);
    }
    
    if(Math.abs(vel.x) > MAX_VELOCITY) {      
      vel.x = Math.signum(vel.x) * MAX_VELOCITY;
      player.setLinearVelocity(vel.x, vel.y);
    }
    
    if (jump) {
      jump = false;
      if(grounded) {
        player.setLinearVelocity(vel.x, 0); 
        player.setTransform(pos.x, pos.y, 0);
        player.applyLinearImpulse(0, jumpPower, pos.x, pos.y, true);
      }
    }

    if (!grounded) {
      this.state = State.Jumping;
    } else if(keyLeft || keyRight) {
      this.state = State.Walking;
    } else {
      this.state = State.Idle;
    }
    
    if (keyLeft) {
      playerAnimation.setFlipX(true);
    } else if (keyRight) {
      playerAnimation.setFlipX(false);
    }
    
    if (this.state == State.Jumping) {
      playerAnimation.play("JUMP");
    } else if (this.state == State.Walking) {
      playerAnimation.play("WALKING");
    } else {
      playerAnimation.play("IDLE");
    }
    
    player.setAwake(true);
  }

  private boolean isPlayerGrounded(float deltaTime) {    
    List<Contact> contactList = getOwner().getLevel().getPsychicsManager().getWorld().getContactList();
    for(int i = 0; i < contactList.size(); i++) {
      Contact contact = contactList.get(i);
      if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
         contact.getFixtureB() == playerSensorFixture)) {       
 
        Vector2 pos            = player.getPosition();
        WorldManifold manifold = contact.getWorldManifold();
        boolean below = true;
        slopeFactor = 0.0f;
        for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
          slopeFactor = Math.abs(Math.round((pos.x - manifold.getPoints()[j].x) * 10000));
          //Gdx.app.log(TAG, "Slope: "+ slopeFactor);
          below       &= (manifold.getPoints()[j].y < pos.y - sensorHeight && slopeFactor <= MAX_SLOPE);
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
    
    this.sensorHeight          = (e.getWidth() / 2 * PsychicsManager.WORLD_TO_BOX);
    BodyDef def                = new BodyDef();
    def.type                   = BodyType.DynamicBody;
    def.position.set(e.getCenterPositionX() * PsychicsManager.WORLD_TO_BOX, e.getCenterPositionY() * PsychicsManager.WORLD_TO_BOX);
    
    Body box                   = e.getLevel().getPsychicsManager().getWorld().createBody(def);
    PolygonShape poly          = new PolygonShape();   
    float widthInMeters        = e.getWidth()  * PsychicsManager.WORLD_TO_BOX / 2.0f;
    float heightInMeters       = e.getHeight() * PsychicsManager.WORLD_TO_BOX / 2.0f;
    float sensorRadius         = widthInMeters;
    //Gdx.app.log(TAG, "Size in meters: "+ widthInMeters + "x"+heightInMeters);
    
    poly.setAsBox(widthInMeters, heightInMeters);
    
    FixtureDef fixDef          = new FixtureDef();
    fixDef.shape               = poly;
    fixDef.density             = playerWeight;
    fixDef.filter.categoryBits = PsychicsManager.FILTER_CATEGORY_PLAYER;
    playerPhysicsFixture       = box.createFixture(fixDef);
    poly.dispose();     
 
    CircleShape circle         = new CircleShape();   
    circle.setRadius(sensorRadius+0.032f);
    
    circle.setPosition(new Vector2(0, sensorPositionY ));
    
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
    
    playerAnimation = (AnimatedSpriteComponent) getOwner().getComponent(AnimatedSpriteComponent.class);
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void configure(Map<String, Object> map) {
    this.width              = Integer.parseInt((String)map.get("width"));
    this.height             = Integer.parseInt((String)map.get("height"));
    this.playerWeight       = Float.parseFloat((String)map.get("weight"));
    this.moveSpeed          = Float.parseFloat((String)map.get("move-speed"));
    this.jumpPower          = Float.parseFloat((String)map.get("jump-power"));
    this.sensorPositionY    = Float.parseFloat((String)map.get("sensor-offset-y"));
  }

  @Override
  public void render(SpriteBatch batch) {
    Position pos = getOwner().getPosition();
    
    ResourceManager.shared().getFont("CURRIER_NEW").drawMultiLine(batch, "friction: " + playerPhysicsFixture.getFriction() + "\nstate: " + getState().toString() + "\nSlope: " + slopeFactor, pos.x+20, pos.y);
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  @Override
  public void onRemove() {
    Entity owner    = getOwner();
    owner.getLevel().getPsychicsManager().getWorld().destroyBody(player);
  }
}
