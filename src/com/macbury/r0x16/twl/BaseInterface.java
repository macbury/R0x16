package com.macbury.r0x16.twl;

import java.io.IOException;

import org.lwjgl.LWJGLException;

import com.badlogic.gdx.Gdx;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class BaseInterface extends Widget {
  private GUI gui;
  private LWJGLRenderer renderer;
  private ThemeManager themeManager;
  private Button button;
  
  public BaseInterface() {
    try{
        renderer = new LWJGLRenderer();
    }catch(LWJGLException e){
        e.printStackTrace();
    }
  
    gui = new GUI(this, renderer);
    
    try{
      themeManager = ThemeManager.createThemeManager(Gdx.files.internal("assets/theme/theme.xml").file().toURL(), renderer);
    } catch(IOException e){
        e.printStackTrace();
    }
  
    gui.applyTheme(themeManager);
    
    button = new Button("Epic button");
    button.setPosition(100, 100);
    button.setSize(100, 33);
    add(button);
  }
  
  public void update() {
    gui.update();
  }
  
  protected void layout(){
    button.setPosition(100, 100);
    button.setSize(100, 33);
  }
}
