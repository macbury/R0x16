package com.macbury.r0x16.components;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentRenderInterface;
import com.macbury.r0x16.entities.ComponentUpdateInterface;
import com.macbury.r0x16.manager.PrefabManager;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.utils.Position;

public class AnimatedSpriteComponent extends Component implements ComponentRenderInterface {
  private static final String TAG = "AnimatedSpriteComponent";
  private float offsetY           = 0.0f;
  private boolean flipX           = false;
  private boolean flipY           = false;
  private Map<String, Animation> animations;
  private Animation currentAnimation;
  float time = 0.0f;
  public AnimatedSpriteComponent() {
    super();
    animations = new HashMap<String, Animation>();
  }
  

  @Override
  public void render(SpriteBatch batch) {
    if (currentAnimation != null) {
      TextureRegion region = currentAnimation.getKeyFrame(time+=Gdx.graphics.getDeltaTime());
      Position pos = getOwner().getPosition();
      
      batch.draw(region.getTexture(), 
          pos.x, 
          pos.y + offsetY, 
          0.0f, 
          0.0f, 
          (float)region.getRegionWidth(), 
          (float)region.getRegionHeight(), 
          1.0f, 
          1.0f, 
          getOwner().getRotation(), 
          region.getRegionX(), 
          region.getRegionY(), 
          region.getRegionWidth(), 
          region.getRegionHeight(), 
          isFlipX(), 
          isFlipY());
    }
  }

  @Override
  public void setup() {
    
  }

  @Override
  public void reset() {
    
  }

  @Override
  public void configure(Map<String, Object> map) {
    if (map.containsKey("offset-y")) {
      offsetY = Float.parseFloat((String)map.get("offset-y"));
    }
    
    if (map.containsKey(PrefabManager.EXTRA_PAYLOAD)) {
      NodeList childs = (NodeList)map.get(PrefabManager.EXTRA_PAYLOAD);
      int count       = childs.getLength();
      
      for (int i = 0; i < count; i++) {
        Element node               = (Element) childs.item(i);
        TextureAtlas atlas         = ResourceManager.shared().getAtlas((String)node.getAttribute("atlas"));
        Array<AtlasRegion> regions = atlas.findRegions((String)node.getAttribute("region"));
        Animation anim             = new Animation(Float.parseFloat((String)node.getAttribute("duration")), regions);

        switch (node.getAttribute("type")) {
          case "NORMAL":
            anim.setPlayMode(Animation.NORMAL);
          break;

          case "LOOP":
            anim.setPlayMode(Animation.LOOP);
          break;
          
          default:
            Gdx.app.log(TAG, "Unsuported animation type: " + node.getAttribute("type"));
          break;
        }
        animations.put(node.getAttribute("name"), anim);
      }
      
      if (count > 0) {
        currentAnimation = animations.get(animations.keySet().toArray()[0]);
      }
    }
  }


  public void play(String string) {
    this.currentAnimation = animations.get(string);
  }


  public boolean isFlipX() {
    return flipX;
  }


  public void setFlipX(boolean flipX) {
    this.flipX = flipX;
  }


  public boolean isFlipY() {
    return flipY;
  }


  public void setFlipY(boolean flipY) {
    this.flipY = flipY;
  }


  @Override
  public void onRemove() {
    // TODO Auto-generated method stub
    
  }

}
