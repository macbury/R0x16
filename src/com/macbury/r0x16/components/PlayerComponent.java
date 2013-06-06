package com.macbury.r0x16.components;

import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.PsychicsManager;

public class PlayerComponent extends Component implements ComponentUpdateInterface {
  boolean jump = false;
  private Fixture playerPhysicsFixture;
  private Fixture playerSensorFixture;
  private Body player;
  
  @Override
  public void update(float delta) {
    
  }

  @Override
  public void setup() {
    Entity e = getOwner();
    e.setWidth(32);
    e.setHeight(64);
    BodyDef def = new BodyDef();
    def.type    = BodyType.DynamicBody;
    Body box    = e.getLevel().getPsychicsManager().getWorld().createBody(def);
 
    PolygonShape poly    = new PolygonShape();   
    poly.setAsBox(e.getWidth() * PsychicsManager.WORLD_TO_BOX / 2, e.getHeight() * PsychicsManager.WORLD_TO_BOX / 2);
    playerPhysicsFixture = box.createFixture(poly, 1);
    poly.dispose();     
 
    CircleShape circle  = new CircleShape();   
    circle.setRadius(e.getWidth() / 2 * PsychicsManager.WORLD_TO_BOX);
    circle.setPosition(new Vector2(0, -e.getHeight() / 2 * PsychicsManager.WORLD_TO_BOX));
    playerSensorFixture = box.createFixture(circle, 0);   
    circle.dispose();   
 
    box.setBullet(true);
    
    player = box;
    
    player.setTransform(e.getCenteredPositionInMeters(), e.getRotation());
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void configure(Map<String, String> map) {
    // TODO Auto-generated method stub

  }

}
