package com.macbury.r0x16.components;

import java.util.Map;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.manager.PsychicsManager;

public class BallDynamicBodyComponent extends DynamicBodyComponent {
  @Override
  protected Shape getShape() {
    Entity owner          = getOwner();
    CircleShape ballShape = new CircleShape();
    float width           = Math.round(owner.getWidth() * PsychicsManager.WORLD_TO_BOX / 2);
    ballShape.setRadius(width);
    return ballShape;
  }
 
}
