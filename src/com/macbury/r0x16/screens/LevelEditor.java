package com.macbury.r0x16.screens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MenuBar;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.macbury.r0x16.Core;
import com.macbury.r0x16.entities.Entity;
import com.macbury.r0x16.game_editor.LevelEditorFrame;
import com.macbury.r0x16.manager.LevelManager;
import com.macbury.r0x16.manager.PrefabManager;
import com.macbury.r0x16.manager.ResourceManager;

public class LevelEditor implements Screen, InputProcessor {
  private Stage stage;
  private OrthographicCamera camera;
  private ShapeRenderer shapeRenderer;
  private LevelManager levelManager;
  private static final Color GRID_COLOR     = new Color(1, 1, 1, 0.1f);
  private static final Color GRID_BIG_COLOR = new Color(1, 1, 1, 0.15f);
  private static final int GRID_SIZE        = 32;
  private static final String TAG = "LevelEditor";
  
  private Entity entityBrush;
  private Vector3 tempMouseVector;
  private LevelEditorFrame levelEditorFrame;
  
  public LevelEditor(LevelEditorFrame levelEditorFrame) {
    this.levelEditorFrame      = levelEditorFrame;
    stage                      = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    Skin skin                  = ResourceManager.shared().getMainSkin();
    Gdx.input.setInputProcessor(this);
    
    //splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroller, Core.frame.getContentPane());
    //Core.frame.setContentPane(splitPane);
    
    //Stage s = stage;
    //Gdx.input.setInputProcessor(s);
    
    
    newMap();

    shapeRenderer = new ShapeRenderer();
    tempMouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
  }
  
  @Override
  public void render(float delta) {
    levelManager.setLookAt(null);
    levelManager.getPsychicsManager().syncWithEntites();
    levelEditorFrame.hBar.setMaximum((int) levelManager.getSize().getWidth());
    levelEditorFrame.vBar.setMaximum((int) levelManager.getSize().getHeight());
    
    camera.position.x = levelEditorFrame.hBar.getValue(); //+ (Gdx.graphics.getWidth() / 2);
    camera.position.y = levelEditorFrame.vBar.getMaximum() - levelEditorFrame.vBar.getValue(); //+ (Gdx.graphics.getHeight() / 2);
    camera.update();
    
    levelManager.update(delta);
    levelManager.render();
    levelManager.getPsychicsManager().renderDebug();
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    shapeRenderer.setProjectionMatrix(camera.combined);
    
    shapeRenderer.begin(ShapeType.Line);
    
    //shapeRenderer.line(0, 0, 0, 0);
    int gridVeriticalCount  = Math.round(levelManager.getSize().getHeight() / LevelEditor.GRID_SIZE);
    int gridHorizontalCount = Math.round(levelManager.getSize().getWidth() / LevelEditor.GRID_SIZE);
    
    for (int x = 0; x < gridHorizontalCount; x++) {
      if (x % 2 == 0) {
        shapeRenderer.setColor(GRID_BIG_COLOR);
      } else {
        shapeRenderer.setColor(GRID_COLOR);
      }
      shapeRenderer.line(x * LevelEditor.GRID_SIZE, 0, x * LevelEditor.GRID_SIZE, gridVeriticalCount * LevelEditor.GRID_SIZE);
    }
    
    for (int y = 0; y < gridVeriticalCount; y++) {
      if (y % 2 == 0) {
        shapeRenderer.setColor(GRID_BIG_COLOR);
      } else {
        shapeRenderer.setColor(GRID_COLOR);
      }
      shapeRenderer.line(0, y * LevelEditor.GRID_SIZE, gridHorizontalCount * LevelEditor.GRID_SIZE, y * LevelEditor.GRID_SIZE);
    }
    
    shapeRenderer.end();
    Gdx.gl.glDisable(GL10.GL_BLEND);

    tempMouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
    camera.unproject(tempMouseVector);
    
    if (entityBrush != null) {
      entityBrush.setCenterPosition(Math.round(tempMouseVector.x/GRID_SIZE) * GRID_SIZE, Math.round(tempMouseVector.y/GRID_SIZE)* GRID_SIZE);
    }
    
   // Core.frame.setTitle("X: "+ Math.round(tempMouseVector.x) + " Y: " + Math.round(tempMouseVector.y));
    //stage.act(delta);
    //stage.draw();
   // Table.drawDebug(stage);
  }

  @Override
  public void resize(int width, int height) {
    stage.setViewport(width, height, true);
    camera.setToOrtho(false, width, height);
    
  }

  @Override
  public void show() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub

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

  public void saveMap() {
    if (levelManager.getName() != null) {
      levelManager.save();
    } else {
      JFileChooser saveDialog = new JFileChooser();
      saveDialog.setCurrentDirectory(Gdx.files.internal("assets/maps").file());
      saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Map Files", "map");
      saveDialog.setFileFilter(filter);
      if (saveDialog.showSaveDialog(Core.frame) == JFileChooser.APPROVE_OPTION) {
        File file = saveDialog.getSelectedFile();
        Gdx.app.log(TAG, "Selected from panel: "+ file.getName());
      }
    }
  }

  public void newMap() {
    entityBrush                = null;
    levelManager               = new LevelManager();
    levelManager.getPsychicsManager().pause();
    camera                     = levelManager.getCamera();
  }

  @Override
  public boolean keyDown(int keycode) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Gdx.app.log(TAG, "Clicked!");
    
    String prefabName = levelEditorFrame.prefabsList.getSelectedValue();
    Gdx.app.log(TAG, "Selected prefab: "+prefabName);
    if (prefabName != null) {
      entityBrush = levelManager.getEntityManager().build(prefabName);
    }
    
    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    // TODO Auto-generated method stub
    return false;
  }
  
  public void setBrush(String prefab) {
    if (entityBrush != null) {
      entityBrush.destroy();
    } 
    
    entityBrush = levelManager.getEntityManager().build(prefab);
  }
}
