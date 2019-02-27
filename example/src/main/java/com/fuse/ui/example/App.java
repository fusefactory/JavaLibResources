package com.fuse.resources.example;

import java.util.logging.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class App extends PApplet {
  Logger logger;
  private PApplet papplet;
  private PGraphics pg;
  private float timeBetweenFrames;
  private boolean bDrawDebug;

  public static void main( String[] args )
  {
    PApplet.main("com.fuse.resources.example.App");
  }

  public App(){
    super();
    logger = Logger.getLogger("");//App.class.getName());
    logger.setLevel(Level.ALL);
    papplet = this;
  }

  public void settings(){
    size(300, 200, P3D);
  }

  public void setup(){
    papplet.frameRate(30.0f);
    timeBetweenFrames = 1.0f / papplet.frameRate;
    bDrawDebug = false;
  }

  private void update(float dt){
  }

  public void draw(){
    // OF-style; first update app state before rendering
    update(timeBetweenFrames);

  }

  public void mousePressed(){
  }

  public void mouseDragged(){

  }

  public void mouseReleased(){
  }

  public void keyPressed(){
    switch(key){
      case '1': {
        // load
      }

      case '2': {
        // load
      }

      case 'd': {
        bDrawDebug = !bDrawDebug;
        return;
      }
    }
  }
}
