package com.macbury.r0x16.components;

import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;

public class FollowCameraComponent extends Component implements ComponentUpdateInterface {

  @Override
  public void update(float delta) {
    Entity e = getOwner();
    OrthographicCamera camera = e.getLevel().getCamera();
    //camera.position.set(e.getPosition());
    //camera.position.y -= 10;
  }

  @Override
  public void setup() {
    // TODO Auto-generated method stub

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
