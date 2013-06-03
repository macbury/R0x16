package com.macbury.r0x16.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.macbury.r0x16.utils.Position;

public class Entity implements Poolable  {
  private static final String TAG = "Entity";
  private Position position;
  private ArrayList<Component> components;
  private ArrayList<Component> renderComponents;
  private ArrayList<Component> updateComponents;
  
  public Entity() {
    this.position         = new Position();
    this.components       = new ArrayList<Component>();
    this.renderComponents = new ArrayList<Component>();
    this.updateComponents = new ArrayList<Component>();
  }
  
  public Component addComponent(Class<? extends Component> componentKlass) {
    Component component = null;
    try {
      component = componentKlass.newInstance();
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
    this.components.clear();
    this.renderComponents.clear();
    this.updateComponents.clear();
  }
  
  public Position getPosition() {
    return position;
  }
  
  public void setPosition(Position position) {
    this.position = position;
  }
  
  public void render() {
    for (Component component : renderComponents) {
      if (component.isEnabled()) {
        ComponentRenderInterface c = (ComponentRenderInterface)component;
        c.render();
      }
    }
  }
  
  public void update(float delta) {
    for (Component component : updateComponents) {
      if (component.isEnabled()) {
        ComponentUpdateInterface c = (ComponentUpdateInterface)component;
        c.update(delta);
      }
    }
  }
}
