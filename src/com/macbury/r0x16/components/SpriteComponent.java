package com.macbury.r0x16.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentRenderInterface;
import com.macbury.r0x16.utils.Position;

public class SpriteComponent extends Component implements ComponentRenderInterface {
  private TextureRegion region;
  
  @Override
  public void render(SpriteBatch batch) {
    if (region != null) {
      Position pos = getOwner().getPosition();
      batch.draw(region, pos.x, pos.y);
    }
  }

  public void setTexture(TextureRegion region) {
    this.region = region;
  }

  public void setSprite(AtlasRegion findRegion) {
    this.region = findRegion;
  }

}
