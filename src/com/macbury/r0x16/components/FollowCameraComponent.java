package com.macbury.r0x16.components;

import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.entities.Entity;

public class FollowCameraComponent extends Component {


  @Override
  public void setup() {
    Entity e = getOwner();
    e.getLevel().setLookAt(e);
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void configure(Map<String, Object> map) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onRemove() {
    // TODO Auto-generated method stub
    
  }

}
