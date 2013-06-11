package com.macbury.r0x16.components;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.macbury.r0x16.entities.Component;
import com.macbury.r0x16.entities.ComponentRenderInterface;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.utils.Position;

public class DebugComponent extends Component implements ComponentRenderInterface {

  private static final String TAG = "DebugComponent";
  //private ShapeRenderer renderer;

  @Override
  public void render(SpriteBatch batch) {
    //renderer.setProjectionMatrix(getOwner().getLevel().getCamera().combined);
    Position pos = getOwner().getPosition();
    ResourceManager.shared().getFont("CURRIER_NEW").draw(batch, pos.x + "x" + pos.y, pos.x, pos.y);
  }

  @Override
  public void setup() {
    //renderer = new ShapeRenderer();
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
