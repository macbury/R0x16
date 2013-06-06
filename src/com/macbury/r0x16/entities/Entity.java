package com.macbury.r0x16.entities;

import java.util.ArrayList;
import java.util.logging.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.macbury.r0x16.manager.LevelManager;
import com.macbury.r0x16.manager.PsychicsManager;
import com.macbury.r0x16.utils.Position;

public class Entity implements Poolable {
  private static final String TAG = "Entity";
  private Position position;
  private LevelManager    level;
  private ArrayList<Component> components;
  private ArrayList<Component> renderComponents;
  private ArrayList<Component> updateComponents;
  
  private boolean requiredSetup = true;
  private int width;
  private int height;
  private Vector3 positionCache;
  private float rotation = 0.0f;
  
  public Entity() {
    this.position           = new Position();
    this.components         = new ArrayList<Component>();
    this.renderComponents   = new ArrayList<Component>();
    this.updateComponents   = new ArrayList<Component>();
    this.positionCache      = new Vector3();
    requiredSetup           = true;
  }
  
  public Component addComponent(Class<? extends Component> componentKlass) {
    Component component = null;
    requiredSetup = true;
    try {
      component = componentKlass.newInstance();
      component.setOwner(this);
      this.components.add(component);
      if (ComponentRenderInterface.class.isInstance(component)) {
        this.renderComponents.add(component);
      }
      if (ComponentUpdateInterface.class.isInstance(component)) {
        this.updateComponents.add(component);
      }
      
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return component;
  }
  
  public Component getComponent(Class<? extends Component> componentKlass) {
    for (int i = 0; i < this.components.size(); i++) {
      if (componentKlass.isInstance(this.components.get(i))) {
        return this.components.get(i);
      }
    }
    return null;
  }
  
  public boolean removeComponent(Class<? extends Component> componentKlass) {
    boolean removed = false;
    for (int i = 0; i < this.components.size(); i++) {
      Component component = this.components.get(i);
      if (componentKlass.isInstance(component)) {
        this.components.remove(i);
        if (ComponentRenderInterface.class.isInstance(component)) {
          this.renderComponents.remove(component);
        }
        if (ComponentUpdateInterface.class.isInstance(component)) {
          this.updateComponents.remove(component);
        }
        removed = true;
      }
    }
    return removed;
  }
  
  @Override
  public void reset() {
    Gdx.app.log(TAG, "Reseting entity");
    for (Component component : this.components) {
      component.reset();
    }
    requiredSetup = true;
  }
  
  public Position getPosition() {
    return position;
  }
  
  public void setPosition(Position position) {
    this.position = position;
  }
  
  public void render(SpriteBatch batch) {
    for (Component component : renderComponents) {
      if (component.isEnabled()) {
        ComponentRenderInterface c = (ComponentRenderInterface)component;
        c.render(batch);
      }
    }
  }
  
  public void update(float delta) {
    if (requiredSetup) {
      for (Component component : this.components) {
        component.setup();
      }
      requiredSetup = false;
    }
    
    for (Component component : updateComponents) {
      if (component.isEnabled()) {
        ComponentUpdateInterface c = (ComponentUpdateInterface)component;
        c.update(delta);
      }
    }
  }

  public LevelManager getLevel() {
    return level;
  }

  public void setLevel(LevelManager level2) {
    this.level = level2;
  }
  
  public Vector3 getRightBottomPoint() {
    this.positionCache.set(getPosition());
    this.positionCache.add(getWidth(), getHeight(), getPosition().z);
    return this.positionCache;
  }
  
  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public boolean visibleByCamera(OrthographicCamera camera) {
    return camera.frustum.pointInFrustum(getPosition()) || camera.frustum.pointInFrustum(getRightBottomPoint());
  }

  public void setPositionInMeters(float x, float y) {
    setPosition(x / PsychicsManager.WORLD_TO_BOX, y / PsychicsManager.WORLD_TO_BOX);
  }

  public void setPosition(float x, float y) {
    this.position.x = x;
    this.position.y = y;
  }

  public void setRotation(float angle) {
    this.rotation = angle;
  }

  public float getRotation() {
    return this.rotation;
  }
  
  public float getCenterPositionX() {
    return this.position.x + this.getWidth() / 2;
  }
  
  public float getCenterPositionY() {
    return this.position.y + this.getHeight() / 2;
  }

  public void setCenterPosition(float x, float y) {
    this.position.x = Math.round(x - this.getWidth() / 2);
    this.position.y = Math.round(y - this.getHeight() / 2);
  }

  public Vector2 getCenteredPositionInMeters() {
    return new Vector2(this.getCenterPositionX() * PsychicsManager.WORLD_TO_BOX, this.getCenterPositionY() * PsychicsManager.WORLD_TO_BOX);
  }
  
}
