package com.macbury.r0x16.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class LevelManager {
  private static final String TAG = "LevelManager";
  private EntityManager entityManager;
  private PsychicsManager psychicsManager;
  private OrthographicCamera camera;
  
  public LevelManager(String filename) {
    Gdx.app.log(TAG, "Loading level: " + filename);
    camera        = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);  
    camera.update();
    
    setEntityManager(new EntityManager(this));
    setPsychicsManager(new PsychicsManager(this));
  }
  
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }
  
  public void render() {
    camera.update();
    entityManager.render();
  }
  
  public void update(float delta) {
    entityManager.update(delta);
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
}
