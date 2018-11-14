package com.fuse.resources;

import java.io.File;

// import java.util.regex.Pattern;
// import java.util.regex.Matcher;
import com.fuse.vlcplayer.VLCPlayer;

public class VlcPlayerSource extends BaseResourceSource<String, VLCPlayer> {
  private String audioOutput = null;
  private String audioDevice = null;

  public VlcPlayerSource(){
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

  public VlcPlayerSource(String audioOutput, String audioDevice) {
    this();
    this.setAudioOutput(audioOutput, audioDevice);
  }

  public String getAudioOutput() {
	  return this.audioOutput;
  }
  
  public String getAudioDevice() {
	  return this.audioDevice;
  }

  public void setAudioOutput(String output, String device) {
    this.audioOutput = output == null || output.equals("") ? null : output;
    this.audioDevice = device == null || device.equals("") ? null : device;
  }

  @Override
  public void remove(VLCPlayer player) {
    if (player != null)
    {
      player.close();
      player = null;
    }

    super.remove(player);
  }

  /** The main loader routine */
  private VLCPlayer _load(String url){
    // this method is called by the loader which is registered
    // in the constructor method

    // the 'url' can be normal a file path, like;
    //    /tmp/video.mp4 (absolute)
    //    data/video.mov (relative)

    String filePath = url;

    // url query processing
    // String query = null;
    if(url.contains("?")){
      String[] parts = filePath.split("\\?");
      filePath = parts[0];
      // query = parts[1];
    }


    // no url parameters currently supported...
    if (!new File(filePath).exists()) {
      logger.info("Could not load video because file doesn't exist: "+filePath);
      return null;
    }

    // let do this
    logger.fine("Loading movie: "+url);
    VLCPlayer player;

    try{
      // newMovie = new Movie(this.papplet, filePath);
      player = new VLCPlayer(filePath);
    } catch(java.lang.UnsatisfiedLinkError exc){
      this.logger.warning("Failed to load movie '"+ filePath+"', probably GStreamer binaries aren't properly loaded:\n\n");
      this.logger.warning(exc.getMessage());
      exc.printStackTrace();
      this.logger.warning("\n\n");

      player = null;
    } catch(Exception exc){
      this.logger.warning("Failed to load movie '"+ filePath+"':\n\n");
      this.logger.warning(exc.getMessage());
      exc.printStackTrace();
      this.logger.warning("\n\n");

      player = null;
    } catch(java.lang.NoClassDefFoundError err) {
      this.logger.warning("Failed to load movie '"+ filePath+"':\n\n");
      this.logger.warning(err.getMessage());
      err.printStackTrace();
      this.logger.warning("\n\n");

      player = null;
    }

    if (player != null) {
      this.configureAudioOutput(player);
    }

    return player;
  }

  private void configureAudioOutput(VLCPlayer player) {
    if (this.audioOutput == null) return;
    player.setAudioOutput(this.audioOutput);
    player.setAudioOutputDevice(this.audioOutput,  this.audioDevice);
  }
}
