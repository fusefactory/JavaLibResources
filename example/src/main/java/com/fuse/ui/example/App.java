package com.fuse.resources.example;

import java.util.logging.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import com.fuse.resources.AudioSource;
import ddf.minim.AudioPlayer;

public class App extends PApplet {
  Logger logger;
  private PApplet papplet;
  private PGraphics pg;
  private float timeBetweenFrames;
  private boolean bDrawDebug;

  private AudioSource audioSource;

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

    this.audioSource = new AudioSource(this.papplet);
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
        this.logger.info("load1");
        this.audioSource.getAsync(dataPath("test.mp3")).withSingleResult((AudioPlayer player) -> {
          this.logger.info("load1-done");
          player.play();
          this.logger.info("load1 started");
          player.close();
          this.logger.info("load1 closed");
        });
        return;
      }

      case '2': {
        // load
        this.logger.info("load2");
        this.audioSource.getAsync(dataPath("test.mp3")).withSingleResult((AudioPlayer player) -> {
          this.logger.info("load2-done");
          player.play();
          this.logger.info("load2 started");
          player.close();
          this.logger.info("load2 closed");
        });
        return;
      }

      case 'd': {
        bDrawDebug = !bDrawDebug;
        return;
      }
    }
  }
}
