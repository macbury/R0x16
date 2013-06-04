package com.macbury.r0x16.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.PsychicsManager;

public class StaticBodyComponent extends Component {
  private BodyDef bodyDef;
  private PolygonShape shape;
  private Body body;
  
  public StaticBodyComponent() {
    bodyDef = new BodyDef();
  }
  
  @Override
  public void setup() {
    Entity owner = getOwner();
    bodyDef.position.set(owner.getCenterPositionX() * PsychicsManager.WORLD_TO_BOX, owner.getCenterPositionY() * PsychicsManager.WORLD_TO_BOX);
    body = owner.getLevel().getPsychicsManager().getWorld().createBody(bodyDef);
    
    shape = new PolygonShape();
    shape.setAsBox(Math.round(owner.getWidth() * PsychicsManager.WORLD_TO_BOX / 2), Math.round(owner.getHeight() * PsychicsManager.WORLD_TO_BOX / 2));
    body.createFixture(shape, 0.0f);
    shape.dispose();
    this.body.setUserData(owner);
  }
  
  
}
