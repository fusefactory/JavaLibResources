package com.fuse.resources;

import java.util.function.Function;

import processing.core.PApplet;
import ddf.minim.Minim;
import ddf.minim.AudioPlayer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

/**
  * Audio loading interface which takes care
  * of loading from disk and caching and providing an easy
  * (both synchronous and asynchronous) interface for accessing.
  */
public class AudioSource extends BaseResourceSource<String, AudioPlayer> {

  private Function<String,AudioPlayer> fileLoader;

  public AudioSource(){
    // super.setCacheEnabled(true);

    // here we register the collection's loader logic, which is called when
    // an item is requested that is not cached.
    // This synchronous loader will automatically be converted into a threaded asynchronous
    // loader by the asyncFacade class. The sync loader will be used when the getSync
    // method is called, the async loader will be used when the getAsync method is called.
    setLoader((String url) -> {
      return this._load(url);
    });
  }

  public AudioSource(Minim minim) {
    this();
    this.setMinim(minim);
  }

  public AudioSource(PApplet papplet){
	  this();
	  this.setPapplet(papplet);
  }

  public AudioSource(PApplet papplet, String soundCardName){
    this();
    this.setPapplet(papplet, soundCardName);
  }

  public void setPapplet(PApplet papplet){
    this.setPapplet(papplet, "");
  }

  /**
   * Set the papplet instance the can be used to initialize the
   * Minim audio interface using a specified soundcard
   *
   * @param papplet the PApplet instance to use
   * @param soundCardName a string that specifies which soundcard to use (set to empty string for default)
   */
  public void setPapplet(PApplet papplet, String soundCardName){
    // this.papplet = papplet;
    Minim minim = new Minim(papplet);

    // find soundcard interface to use
    for (Mixer.Info info : AudioSystem.getMixerInfo()) {
      // match by mixerInfo name
      if (info.getName().toLowerCase().equals(soundCardName.toLowerCase())) {
        Mixer mixer = AudioSystem.getMixer(info);
        minim.setOutputMixer(mixer);
        break;
      }
    }

    this.setMinim(minim);
  }

  /**
   * Configures the resource loader to use the given minim instance for loading audio files
   * @param minim
   */
  public void setMinim(Minim minim) {
     this.fileLoader = (String filepath) -> minim.loadFile(filepath);
   }

  public void setFileLoader(Function<String, AudioPlayer> func) {
    this.fileLoader = func;
  }

  /** The main loader routine that process the url */
  private AudioPlayer _load(String url){
    // returning null implies a failure to load resource
    if(this.fileLoader == null){
      logger.warning("no audio file loader lambda, can't load: " + url);
      return null;
    }

    String filePath = url;

    // url query processing
    // String query = null;
    if(url.contains("?")){
      String[] parts = filePath.split("\\?");
      filePath = parts[0];
      // query = parts[1];
    }

    // no url parameters currently supported...

    // let do this
    logger.info("Loading audio file: "+url);
    AudioPlayer audioPlayer = this.fileLoader.apply(filePath);

    if(audioPlayer == null){
      logger.warning("Loading audio file failed: "+filePath);
      return null;
    }

    return audioPlayer;
  }
}
