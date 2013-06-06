package com.macbury.r0x16.components;

import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.PsychicsManager;
import com.macbury.r0x16.manager.ResourceManager;

public class DynamicBodyComponent extends Component {
  private Body body;
  private Fixture fixture;
  private FixtureDef fixtureDef;
  
  public DynamicBodyComponent() {
    setFixtureDef(ResourceManager.shared().getFixtureDef("CUBE"));
  }
  
  @Override
  public void setup() {
    Entity owner    = getOwner();
    BodyDef bodyDef = new BodyDef();
    bodyDef.type    = BodyType.DynamicBody;
    
    bodyDef.position.set(owner.getCenterPositionX() * PsychicsManager.WORLD_TO_BOX, owner.getCenterPositionY() * PsychicsManager.WORLD_TO_BOX);
    this.body = owner.getLevel().getPsychicsManager().getWorld().createBody(bodyDef);
    
    applyFixtureDef(getFixtureDef());
    this.body.setUserData(owner);
  }

  @Override
  public void reset() {
    body.resetMassData();
  }


  @Override
  public void configure(Map<String, String> map) {
    setFixtureDef(ResourceManager.shared().getFixtureDef(map.get("material")));
  }


  private void applyFixtureDef(FixtureDef fixtureDef) {
    if (this.fixture != null) {
      body.destroyFixture(fixture);
    }
    body.resetMassData();
    fixtureDef.shape = getShape();
    fixtureDef.filter.categoryBits = PsychicsManager.FILTER_MASK_SCENERY;
    fixture          = body.createFixture(fixtureDef);
  }


  protected Shape getShape() {
    Entity owner = getOwner();
    
    float width  = Math.round(owner.getWidth() * PsychicsManager.WORLD_TO_BOX / 2);
    float height = Math.round(owner.getHeight() * PsychicsManager.WORLD_TO_BOX / 2);
    
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(width, height);
    return shape;
  }


  public FixtureDef getFixtureDef() {
    return fixtureDef;
  }


  public void setFixtureDef(FixtureDef fixtureDef) {
    this.fixtureDef = fixtureDef;
  }

  public Body getBody() {
    // TODO Auto-generated method stub
    return body;
  }

}
