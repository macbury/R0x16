package com.macbury.r0x16.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.macbury.r0x16.entities.Entity;

public class LevelManager {
  private static final String TAG = "LevelManager";
  private String name;
  private EntityManager entityManager;
  private PsychicsManager psychicsManager;
  private OrthographicCamera camera;
  private Entity followEntity;
  private Rectangle size;
  
  public LevelManager() {
    camera        = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);  
    camera.update(true);
    
    setEntityManager(new EntityManager(this));
    setPsychicsManager(new PsychicsManager(this));
    setSize(new Rectangle(0, 0, 3000, 2000));
  }
  
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }
  
  public void render() {
    if (followEntity != null) {
      camera.position.set(followEntity.getPosition().x, followEntity.getPosition().y, 0);
    }
    camera.update();
    entityManager.render();
    psychicsManager.render();
  }
  
  public void update(float delta) {
    entityManager.update(delta);
    psychicsManager.update(delta);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  private void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public PsychicsManager getPsychicsManager() {
    return psychicsManager;
  }

  private void setPsychicsManager(PsychicsManager psychicsManager) {
    this.psychicsManager = psychicsManager;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }

  public void setLookAt(Entity e) {
    this.followEntity = e;
  }

  public Rectangle getSize() {
    return size;
  }

  public void setSize(Rectangle size) {
    this.size = size;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void save() {
    Gdx.app.log(TAG, "Saving map:"+ getName());
  }
}
