package com.macbury.r0x16.entities;

public abstract class Component {
  private boolean enabled = true;
  private Entity owner;
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Entity getOwner() {
    return owner;
  }

  public void setOwner(Entity owner) {
    this.owner = owner;
  }
}
