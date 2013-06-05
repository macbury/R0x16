package com.macbury.r0x16.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.macbury.r0x16.entities.Component;

public class PrefabFactor {
  private String id;
  private Map<Class<? extends Component>, Map<String, String>> components;
  private ArrayList<Class<? extends Component>> componentsOrderList;
  
  public PrefabFactor(String nid) {
    this.id = nid;
    componentsOrderList = new ArrayList<Class<? extends Component>>();
    setComponents(new HashMap<Class<? extends Component>, Map<String, String>>());
  }
  
  public Map<Class<? extends Component>, Map<String, String>> getComponents() {
    return components;
  }
  
  private void setComponents(Map<Class<? extends Component>, Map<String, String>> components) {
    this.components = components;
  }

  public ArrayList<Class<? extends Component>> getComponentsOrderList() {
    return componentsOrderList;
  }

}
