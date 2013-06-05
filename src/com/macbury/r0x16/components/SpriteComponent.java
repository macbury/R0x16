package com.macbury.r0x16.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentRenderInterface;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.utils.Position;

public class SpriteComponent extends Component implements ComponentRenderInterface {
  private TextureRegion region;
  private Sprite sprite;
  @Override
  public void render(SpriteBatch batch) {
    if (region != null) {
      Position pos = getOwner().getPosition();
      this.sprite.setPosition(pos.x, pos.y);
      this.sprite.setRotation(getOwner().getRotation());
      this.sprite.draw(batch);
      ResourceManager.shared().getFont("CURRIER_NEW").draw(batch, pos.x+"x"+pos.y, pos.x, pos.y);
      //batch.draw(region, pos.x, pos.y, originX, originY, width, height, scaleX, scaleY, rotation)
    }
  }

  public void setTexture(TextureRegion region) {
    this.region = region;
    this.getOwner().setWidth(region.getRegionWidth());
    this.getOwner().setHeight(region.getRegionHeight());
    this.sprite = new Sprite(region);
  }

  @Override
  public void setup() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub
    
  }
}
