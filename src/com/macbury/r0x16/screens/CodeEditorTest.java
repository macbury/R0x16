package com.macbury.r0x16.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.macbury.r0x16.manager.ResourceManager;
import com.macbury.r0x16.widgets.code_editor.CodeEditor;

public class CodeEditorTest implements Screen {
  private Stage stage;
  private OrthographicCamera camera;
  
  public CodeEditorTest() {
    this.stage = new Stage();
    
    Skin skin                  = ResourceManager.shared().getMainSkin();
    TextButton startGameButton = new TextButton( "Execute >", skin);
    startGameButton.setX(30);
    startGameButton.setY(710);
    startGameButton.setWidth(120);
    startGameButton.setHeight(60);

   //stage.addActor( startGameButton );
    
    CodeEditor textField = new CodeEditor(skin);
    textField.setText(Gdx.files.internal("assets/example.js").readString());
    textField.setX(30);
    textField.setY(40);
    textField.setWidth(1300);
    textField.setHeight(700);
    
    textField.addToStage(stage);
    
    camera = new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.update();
    
    Gdx.input.setInputProcessor(stage);
  }
  
  @Override
  public void render(float delta) {
    camera.update();
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.setViewport(width, height, true);
  }

  @Override
  public void show() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    Gdx.input.setInputProcessor(null);
  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

}
