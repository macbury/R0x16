package com.macbury.r0x16.components;

import java.util.Map;

import box2dLight.Light;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.macbury.r0x16.entities.Component;

public class LightComponent extends Component {

  private static final String TAG = "LightComponent";

  @Override
  public void setup() {
    DynamicBodyComponent component = (DynamicBodyComponent) getOwner().getComponent(DynamicBodyComponent.class);
    Light light = new PointLight(getOwner().getLevel().getPsychicsManager().getLight(), 32);
    light.setDistance(12);
    light.setActive(true);
    light.setStaticLight(false);
    light.setSoft(true);
    Body body = component.getBody();
    if (body == null) {
      Gdx.app.error(TAG, "Body cannot be null!");
    }
    light.attachToBody(body, 0, 0f);
    light.setColor(1,1,1, 1f);
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
