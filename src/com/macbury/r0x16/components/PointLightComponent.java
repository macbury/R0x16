package com.macbury.r0x16.components;

import java.util.Map;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.manager.PsychicsManager;

public class PointLightComponent extends Component {
  private static final String TAG    = "PointLightComponent";
  private static final int RAY_COUNT = 32;
  private boolean isStatic           = false;
  private float distance             = 10.0f;
  private Color color                = Color.WHITE;
  
  @Override
  public void setup() {
    DynamicBodyComponent component = (DynamicBodyComponent) getOwner().getComponent(DynamicBodyComponent.class);
    RayHandler handler = getOwner().getLevel().getPsychicsManager().getLight();
    Light light = new PointLight(handler, RAY_COUNT);
    light.setDistance(distance);
    light.setActive(true);
    light.setStaticLight(isStatic);

    Body body = component.getBody();
    if (body == null) {
      Gdx.app.error(TAG, "Body cannot be null!");
    }
    light.attachToBody(body, 0, 0f);
    light.setColor(color);
   // light.setSoft(true);
    //light.setSoftnessLenght(1.0f);
    Filter filter = new Filter();
    filter.categoryBits = PsychicsManager.FILTER_CATEGORY_LIGHT;
    filter.maskBits     = PsychicsManager.FILTER_MASK_LIGHT;
    light.setContactFilter(filter);
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void configure(Map<String, String> map) {
    isStatic = map.get("static")=="true";
    distance = Float.parseFloat(map.get("distance"));
    float r = Float.parseFloat(map.get("red"));
    float g = Float.parseFloat(map.get("green"));
    float b = Float.parseFloat(map.get("blue"));
    float a = Float.parseFloat(map.get("alpha"));
    color   = new Color(r, g, b, a);
  }

}
