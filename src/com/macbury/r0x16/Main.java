package com.macbury.r0x16;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

  public static void main(String[] args) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title     = "R0x16";
    config.useGL20   = true;
    config.width     = 1366;
    config.height    = 768;
    config.resizable = false;
    config.samples   = 0;
    config.vSyncEnabled = true;
    config.fullscreen = false;
    LwjglApplication app = new LwjglApplication(Core.shared(), config);
    
    //WindowUtilities.setNativeLookAndFeel();
    /*JFrame f = new JFrame("This is a test");
    f.setSize(320, 768);
    f.setResizable(false);
    f.setVisible(true);8
    f.setAlwaysOnTop(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
  }
}
