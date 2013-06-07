package com.macbury.r0x16.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.Entity;

public class PrefabManager {
  private static PrefabManager _shared;
  private final static String TAG = "PrefabManager";
  private Map<String, PrefabFactor> prefabs;
  
  public static PrefabManager shared() {
    if (_shared == null) {
      _shared = new PrefabManager();
    }
    return _shared;
  }
  
  public PrefabManager() {
    Gdx.app.log(TAG, "Initializing...");
    this.prefabs = new HashMap<String, PrefabFactor>();
  }
  
  public void load() throws Exception {
    File rawXml = Gdx.files.internal("assets/prefabs.game").file();
    Gdx.app.log(TAG, "Loaded prefabs XML");
    
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder               = null;
    
    try {
      docBuilder = docBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
        throw new Exception("Could not load resources", e);
    }
    Document doc = null;
    try {
        doc = docBuilder.parse(rawXml);
    } catch (SAXException e) {
        throw new Exception("Could not load resources", e);
    } catch (IOException e) {
        throw new Exception("Could not load resources", e);
    }
    
    doc.getDocumentElement ().normalize ();
    
    NodeList listPrefabs = doc.getElementsByTagName("prefab");
    int totalResources = listPrefabs.getLength();
    
    for(int resourceIdx = 0; resourceIdx < totalResources; resourceIdx++){
      Node prefabNode = listPrefabs.item(resourceIdx);
      Element prefabElement = (Element)prefabNode;
      addPrefab(prefabElement);
    }
  }

  private void addPrefab(Element element) {
    String id           = element.getAttribute("id");
    PrefabFactor factor = new PrefabFactor(id);
    Gdx.app.log(TAG, "Loading prefab: "+ id);
    
    NodeList listComponents = element.getElementsByTagName("component");
    int componentCount      = listComponents.getLength();
    
    for (int i = 0; i < componentCount; i++) {
      Element componentElement = (Element) listComponents.item(i);
      try {
        String componentName                  = componentElement.getAttribute("type");
        Class<? extends Component> component  = (Class<? extends Component>)Class.forName("com.macbury.r0x16.components."+componentName);
        NamedNodeMap attr                     = componentElement.getAttributes();
        HashMap<String, String> options       = new HashMap<String, String>();
        
        for (int j = 0; j < attr.getLength(); j++) {
          Node node = attr.item(j);
          options.put(node.getNodeName(), node.getTextContent());
        }
        factor.getComponents().put(component, options);
        factor.getComponentsOrderList().add(component);
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    this.prefabs.put(id, factor);
  }
  
  public Entity build(String id) {
    PrefabFactor factor = this.prefabs.get(id);
    Entity e            = new Entity();
    e.id                = id;
    for (Class<? extends Component> componentKlass : factor.getComponentsOrderList()) {
      Component component = e.addComponent(componentKlass);
      component.configure( factor.getComponents().get(componentKlass) );
    }
    
    return e;
  }

}
