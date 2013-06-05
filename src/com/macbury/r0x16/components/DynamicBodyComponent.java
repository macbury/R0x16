package com.macbury.r0x16.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.PsychicsManager;
import com.macbury.r0x16.manager.ResourceManager;

public class DynamicBodyComponent extends Component {
  private Body body;


  @Override
  public void setup() {
    Entity owner = getOwner();
    BodyDef bodyDef = new BodyDef();
     // We set our body to dynamic, for something like ground which doesnt move we would set it to StaticBody
    bodyDef.type = BodyType.DynamicBody;
     // Set our body's starting position in the world
    float width  = Math.round(owner.getWidth() * PsychicsManager.WORLD_TO_BOX / 2);
    float height = Math.round(owner.getHeight() * PsychicsManager.WORLD_TO_BOX / 2);
    bodyDef.position.set(owner.getCenterPositionX() * PsychicsManager.WORLD_TO_BOX, owner.getCenterPositionY() * PsychicsManager.WORLD_TO_BOX);
    
     // Create our body in the world using our body definition
    this.body = owner.getLevel().getPsychicsManager().getWorld().createBody(bodyDef);
    //body.setAngularDamping(0.001f);
     // Create a circle shape and set its radius to 6
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(width, height);
    
    FixtureDef fixtureDef = ResourceManager.shared().getFixtureDef("CUBE");
    fixtureDef.shape = shape;
    body.createFixture(fixtureDef);
    
     // Remember to dispose of any shapes after you're done with them!
     // BodyDef and FixtureDef don't need disposing, but shapes do.
    shape.dispose();
    this.body.setUserData(owner);
  }


  @Override
  public void reset() {
    // TODO Auto-generated method stub
    
  }

}
