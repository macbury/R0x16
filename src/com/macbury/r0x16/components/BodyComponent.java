package com.macbury.r0x16.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.Entity;

public abstract class BodyComponent extends Component {
  private static final String TAG = "BodyComponent";
  protected Body body;
  
  public Body getBody() {
    return body;
  }
  
  @Override
  public void onRemove() {
    if (body != null) {
      Gdx.app.log(TAG, "Removing body");
      Entity owner    = getOwner();
      owner.getLevel().getPsychicsManager().getWorld().destroyBody(body);
    }
  }
}
