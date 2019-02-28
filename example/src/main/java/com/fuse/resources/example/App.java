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

  private AudioPlayer p1 = null;
  private AudioPlayer p2 = null;

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
    background(0);
    // OF-style; first update app state before rendering
    update(timeBetweenFrames);

    text("press 1 to load audio player 1\npress 2 to stop audio player 1\npress 3 to load audio player 2\npress 4 to stop audio player 2", 30, 20);
    if (this.p1 != null) rect(10,20,10,10);
    if (this.p2 != null) rect(10,50,10,10);

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
          // player.close();
          // this.logger.info("load1 closed");

          if (this.p1 != null) {
            // this.p1.stop();
            this.p1.close();
          }

          this.p1 = player;
        });
        return;
      }

      case '2': {
        if (this.p1 != null) {
          this.p1.close();
          this.p1 = null;
        }
        return;
      }

      case '3': {
        // load
        this.logger.info("load2");
        this.audioSource.getAsync(dataPath("test.mp3")).withSingleResult((AudioPlayer player) -> {
          this.logger.info("load2-done");
          player.play();
          this.logger.info("load2 started");

          if (this.p2 != null) {
            // this.p2.stop();
            this.p2.close();
          }

          this.p2 = player;
        });
        return;
      }

      case '4': {
        if (this.p2 != null) {
          this.p2.close();
          this.p2 = null;
        }

        return;
      }

      case '5': {
        this.logger.info("load3 - sequence");

        this.logger.info("start async 1");
        this.audioSource.getAsync(dataPath("test.mp3")).withSingleResult((AudioPlayer player) -> {
          this.logger.info("async 1-done");
          this.p1 = player;
          this.logger.info("async 1-close");
          this.p1.close();
        });

        this.logger.info("start async 2");
        this.audioSource.getAsync(dataPath("test.mp3")).withSingleResult((AudioPlayer player2) -> {
          this.logger.info("async 2-done");
          this.p2 = player2;
          this.logger.info("async 2 play");
          // if (this.p1 != null) {
          //   this.logger.info("async 2 close p1 first");
          //   this.p1.close();
          // }
          this.p2.play();
        });

      }

      case 'd': {
        bDrawDebug = !bDrawDebug;
        return;
      }
    }
  }
}
