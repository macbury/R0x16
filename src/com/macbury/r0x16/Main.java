package com.macbury.r0x16;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.macbury.r0x16.utils.PowerFrame;

public class Main {

  public static void main(String[] args) {
    Core.shared().bootstrap(args);
    //WindowUtilities.setNativeLookAndFeel();
    /*JFrame f = new JFrame("This is a test");
    f.setSize(320, 768);
    f.setResizable(false);
    f.setVisible(true);8
    f.setAlwaysOnTop(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
  }
}
