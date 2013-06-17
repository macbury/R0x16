package com.macbury.r0x16.utils;


/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;

/** Wraps an {@link LwjglCanvas} in a resizable {@link JFrame}. */
public class PowerFrame extends JFrame {
  LwjglCanvas lwjglCanvas;

  public PowerFrame (ApplicationListener listener, String title, int width, int height, boolean useGL2) {
    super(title);
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = title;
    config.width = width;
    config.height = height;
    config.useGL20 = useGL2;
    construct(listener, config);
  }

  public PowerFrame (ApplicationListener listener, LwjglApplicationConfiguration config) {
    super(config.title);
    construct(listener, config);
  }

  private void construct (ApplicationListener listener, LwjglApplicationConfiguration config) {
    lwjglCanvas = new LwjglCanvas(listener, config) {
      protected void stopped () {
        PowerFrame.this.dispose();
      }

      protected void setTitle (String title) {
        PowerFrame.this.setTitle(title);
      }

      protected void setDisplayMode (int width, int height) {
        PowerFrame.this.getContentPane().setPreferredSize(new Dimension(width, height));
        PowerFrame.this.getContentPane().invalidate();
        PowerFrame.this.pack();
        PowerFrame.this.setLocationRelativeTo(null);
        updateSize(width, height);
      }

      protected void resize (int width, int height) {
        updateSize(width, height);
      }

      protected void start () {
        PowerFrame.this.start();
      }

      protected void exception (Throwable t) {
        PowerFrame.this.exception(t);
      }
    };

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run () {
        Runtime.getRuntime().halt(0); // Because fuck you, deadlock causing Swing shutdown hooks.
      }
    });

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    getContentPane().setPreferredSize(new Dimension(config.width, config.height));

    initialize();
    pack();
    Point location = getLocation();
    if (location.x == 0 && location.y == 0) setLocationRelativeTo(null);
    lwjglCanvas.getCanvas().setSize(getSize());

    // Finish with invokeLater so any LwjglFrame super constructor has a chance to initialize.
    EventQueue.invokeLater(new Runnable() {
      public void run () {
        addCanvas();
        setVisible(true);
        lwjglCanvas.getCanvas().requestFocus();
      }
    });
  }

  protected void exception (Throwable ex) {
    ex.printStackTrace();
    lwjglCanvas.stop();
  }

  /** Called before the JFrame is made displayable. */
  protected void initialize () {
  }

  /** Adds the canvas to the content pane. This triggers addNotify and starts the canvas' game loop. */
  protected void addCanvas () {
    getContentPane().add(lwjglCanvas.getCanvas());
  }

  /** Called after {@link ApplicationListener} create and resize, but before the game loop iteration. */
  protected void start () {
  }

  /** Called when the canvas size changes. */
  public void updateSize (int width, int height) {
  }

  public LwjglCanvas getLwjglCanvas () {
    return lwjglCanvas;
  }
}