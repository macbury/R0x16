package com.macbury.r0x16.game_editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.macbury.r0x16.Core;
import com.macbury.r0x16.manager.PrefabManager;
import com.macbury.r0x16.screens.LevelEditor;
import com.macbury.r0x16.utils.PowerFrame;

public class LevelEditorFrame extends JFrame implements ActionListener, ListSelectionListener {
  private static final String TAG             = "LevelEditor";
  private static final String NEW_MAP_ACTION  = "New map";
  private static final String SAVE_MAP_ACTION = "Save map";
  private LwjglCanvas lwjglCanvas;
  private JList<String> prefabsList;
  private JScrollBar vBar;
  private JScrollBar hBar;
  private JScrollPane listScroller;
  private Screen levelEditor;
  public LevelEditorFrame(LwjglApplicationConfiguration config) {
    super("Game Editor");
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    
    this.setResizable(true);
    this.setMinimumSize(new Dimension(1280, 700));
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    construct(Core.shared(), config);
  }

  private void addToolbar() {
    JToolBar toolBar = new JToolBar("Tools");
    toolBar.add(makeToolbarButton("document-new", NEW_MAP_ACTION, NEW_MAP_ACTION, NEW_MAP_ACTION));
    toolBar.add(makeToolbarButton("document-save", SAVE_MAP_ACTION, SAVE_MAP_ACTION, SAVE_MAP_ACTION));
    this.add(toolBar, BorderLayout.NORTH);
  }
  
  protected JButton makeToolbarButton(String imageName, String actionCommand, String toolTipText, String altText) {
    URL imageURL = null;
    try {
      imageURL = Gdx.files.internal("assets/editor/"+imageName+".png").file().toURL();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    //Create and initialize the button.
    JButton button = new JButton();
    button.setActionCommand(actionCommand);
    button.setToolTipText(toolTipText);
    button.addActionListener(this);
    
    if (imageURL != null) {                      //image found
      button.setIcon(new ImageIcon(imageURL, altText));
    } else {                                     //no image found
      button.setText(altText);
    }
    
    return button;
  }

  private JMenuBar buildMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    JMenuItem menuItem = new JMenuItem(NEW_MAP_ACTION, KeyEvent.VK_N);
    menuItem.addActionListener(this);
    menu.add(menuItem);
    menuItem = new JMenuItem(SAVE_MAP_ACTION, KeyEvent.VK_S);
    menuItem.addActionListener(this);
    menu.add(menuItem);
    menuItem = new JMenuItem("Open map", KeyEvent.VK_O);
    menu.add(menuItem);
    return menuBar;
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    // TODO Auto-generated method stub
    
  }
  
  protected void exception (Throwable ex) {
    ex.printStackTrace();
    lwjglCanvas.stop();
  }
  
  private void construct (ApplicationListener listener, LwjglApplicationConfiguration config) {
    lwjglCanvas = new LwjglCanvas(listener, config) {
      protected void stopped () {
        LevelEditorFrame.this.dispose();
      }

      protected void setTitle (String title) {
        LevelEditorFrame.this.setTitle(title);
      }

      protected void setDisplayMode (int width, int height) {
        LevelEditorFrame.this.getContentPane().setPreferredSize(new Dimension(width, height));
        LevelEditorFrame.this.getContentPane().invalidate();
        LevelEditorFrame.this.pack();
        LevelEditorFrame.this.setLocationRelativeTo(null);
        updateSize(width, height);
      }

      protected void resize (int width, int height) {
        updateSize(width, height);
      }

      protected void start () {
        Gdx.app.log(TAG, "On Start");
        LevelEditorFrame.this.start();
      }

      protected void exception (Throwable t) {
        LevelEditorFrame.this.exception(t);
      }
    };

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run () {
        Runtime.getRuntime().halt(0); // Because fuck you, deadlock causing Swing shutdown hooks.
      }
    });

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    getContentPane().setPreferredSize(new Dimension(config.width, config.height));

    pack();
    Point location = getLocation();
    if (location.x == 0 && location.y == 0) setLocationRelativeTo(null);
    lwjglCanvas.getCanvas().setSize(getSize());

    // Finish with invokeLater so any LwjglFrame super constructor has a chance to initialize.
    EventQueue.invokeLater(new Runnable() {
      public void run () {
        Gdx.app.log(TAG, "Add canvas");
        addCanvas();
        setVisible(true);
        lwjglCanvas.getCanvas().requestFocus();
      }
    });
  }
  
  protected void addCanvas() {
    this.setVisible(true);
    this.setJMenuBar(buildMenuBar());
    addToolbar();
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(lwjglCanvas.getCanvas());
    this.vBar = new JScrollBar(JScrollBar.VERTICAL);
    panel.add(vBar, BorderLayout.EAST);
    
    this.hBar = new JScrollBar(JScrollBar.HORIZONTAL);
    panel.add(hBar, BorderLayout.SOUTH);
    
    //hBar.setSize(hBar.getWidth() - vBar.getWidth(), hBar.getHeight());
    JPanel properties = new JPanel();
    JSplitPane sidebarSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addPrefabList(), properties);
    sidebarSplitPane.setDividerLocation(240);
    
    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarSplitPane, panel);
    mainSplitPane.setOneTouchExpandable(true);
    mainSplitPane.setDividerLocation(280);
    mainSplitPane.setEnabled(true);
    add(mainSplitPane);
  }

  private JScrollPane addPrefabList() {
    prefabsList = new JList<String>();
    prefabsList.addListSelectionListener(this);
    prefabsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    prefabsList.setLayoutOrientation(JList.VERTICAL);
    prefabsList.setVisibleRowCount(-1);
    this.listScroller = new JScrollPane(prefabsList);
    return listScroller;
  }

  protected void start() {
    levelEditor = new LevelEditor();
    Core.shared().setScreen(levelEditor);
    prefabsList.setModel(PrefabManager.shared().getListModel());
  }

  public void updateSize (int width, int height) {
  }

  @Override
  public void valueChanged(ListSelectionEvent arg0) {
    // TODO Auto-generated method stub
    
  }

}
