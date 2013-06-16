package com.macbury.r0x16.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MenuBar;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
import com.macbury.r0x16.manager.LevelManager;
import com.macbury.r0x16.manager.PrefabManager;
import com.macbury.r0x16.manager.ResourceManager;

public class LevelEditor implements Screen, ActionListener, ListSelectionListener, InputProcessor {
  private Stage stage;
  private JMenuBar menuBar;
  private OrthographicCamera camera;
  private ShapeRenderer shapeRenderer;
  private LevelManager levelManager;
  private JFrame prefabsFrame;
  private JLabel statusLabel;
  private JScrollBar vBar;
  private JScrollBar hBar;
  private Vector3 tempMouseVector;
  private JList prefabsList;
  private static final Color GRID_COLOR     = new Color(1, 1, 1, 0.1f);
  private static final Color GRID_BIG_COLOR = new Color(1, 1, 1, 0.15f);
  private static final int GRID_SIZE        = 32;
  private static final String TAG           = "LevelEditor";
  
  private Entity entityBrush;
  
  public LevelEditor() {
    levelManager               = new LevelManager("");
    levelManager.getPsychicsManager().pause();
    camera                     = levelManager.getCamera();
    stage                      = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    Skin skin                  = ResourceManager.shared().getMainSkin();
    Gdx.input.setInputProcessor(this);
    
    //Stage s = stage;
    //Gdx.input.setInputProcessor(s);
    
    this.prefabsFrame = new JFrame("Prefabs");

    prefabsFrame.setSize(240, 480);
    prefabsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    prefabsFrame.setType(Type.UTILITY);
    prefabsFrame.setAlwaysOnTop(true);
    prefabsFrame.setVisible(true);
    
    menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    JMenuItem menuItem = new JMenuItem("New map", KeyEvent.VK_N);
    menu.add(menuItem);
    menuItem = new JMenuItem("Save map", KeyEvent.VK_S);
    menu.add(menuItem);
    menuItem = new JMenuItem("Open map", KeyEvent.VK_O);
    menu.add(menuItem);
    
    menu = new JMenu("Tools");
    menuBar.add(menu);
    menuItem = new JMenuItem("Prefabs");
    menuItem.addActionListener(this);
    menuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_D, ActionEvent.ALT_MASK));
    menu.add(menuItem);

    this.vBar = new JScrollBar(JScrollBar.VERTICAL);
    Core.frame.add(vBar, BorderLayout.EAST);
    
    this.hBar = new JScrollBar(JScrollBar.HORIZONTAL);
    Core.frame.add(hBar, BorderLayout.SOUTH);
    
    hBar.setSize(hBar.getWidth() - vBar.getWidth(), hBar.getHeight());
    
    JTextField textField = new JTextField(20);
    prefabsFrame.add(textField);
    
    prefabsList = new JList(PrefabManager.shared().getListModel());
    prefabsList.addListSelectionListener(this);
    prefabsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    prefabsList.setLayoutOrientation(JList.VERTICAL);
    prefabsList.setVisibleRowCount(-1);
    JScrollPane listScroller = new JScrollPane(prefabsList);
    prefabsFrame.add(listScroller);
    
    prefabsFrame.setLocation(10, 115);
    
    Core.frame.setResizable(true);
    Core.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    Core.frame.setJMenuBar(menuBar);
    
    hBar.setMaximum((int) levelManager.getSize().getWidth());
    vBar.setMaximum((int) levelManager.getSize().getHeight());
    vBar.setValue(vBar.getMaximum());
    shapeRenderer = new ShapeRenderer();
    
    tempMouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
  }
  
  @Override
  public void render(float delta) {
    levelManager.setLookAt(null);
    levelManager.getPsychicsManager().syncWithEntites();
    camera.position.x = hBar.getValue(); //+ (Gdx.graphics.getWidth() / 2);
    camera.position.y = vBar.getMaximum() - vBar.getValue(); //+ (Gdx.graphics.getHeight() / 2);
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
    
    Core.frame.setTitle("X: "+ Math.round(tempMouseVector.x) + " Y: " + Math.round(tempMouseVector.y));
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

  @Override
  public void actionPerformed(ActionEvent event) {
    prefabsFrame.setVisible(!prefabsFrame.isVisible());
  }

  @Override
  public void valueChanged(ListSelectionEvent event) {
    String prefabName = (String)prefabsList.getSelectedValue();
    Gdx.app.log(TAG, "Selected prefab: "+prefabName);
    
    if (entityBrush != null) {
      entityBrush.destroy();
    }
    
    entityBrush = levelManager.getEntityManager().build(prefabName);
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
    
    String prefabName = (String)prefabsList.getSelectedValue();
    Gdx.app.log(TAG, "Selected prefab: "+prefabName);
    
    entityBrush = levelManager.getEntityManager().build(prefabName);
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

}
