package com.fuse.resources;

import java.io.File;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Movie;
import com.fuse.cms.AsyncFacade;

/**
  * The applications main images/textures interface which takes care
  * of loading images from disk, caching them, optionally performing transformations
  * on them, and providing an easy (both synchornous and asynchornous) interface for accessing.
  */
public class ImageSource extends BaseResourceSource<String, PImage> {

  // USAGE
  // imageSourceInstance.getAsync("/path/to/local/file")
  //   .withResult((List<PImage> loadedImages) -> {
  //     if(loadedImages.size() > 0){
  //        // do something with the loaded images
  //     }
  //   }
  //   .withSingleResult((PImage loadedImage) -> {
  //     // this callback is only called when the requested image was found
  //     // in the collection or successfully loaded by the async loader. If
  //     // if multiple images were loaded, this callback is called ony by one for every image
  //     // DO SOMETHING WITH LOADED IMAGE
  //   });

  private PApplet papplet;
  private AsyncFacade<Movie, PImage> movieFacade;

  public ImageSource(){
	  this(null);
  }

  public ImageSource(PApplet papplet){
  	this.setPapplet(papplet);
    super.setCacheEnabled(true);
    // this sync loader will automatically be converted into a threaded async loader by AsyncFacade class.
    asyncFacade.setThreadPriority(Thread.MIN_PRIORITY);

    setLoader((String urlString) -> {
    	return this._createImage(urlString);
    });

    this.movieFacade = new AsyncFacade<>();
    this.movieFacade.setSyncLoader((Movie movie) -> {
      return this._createImage(movie);
    });
  }

  public void setPapplet(PApplet papplet){
    this.papplet = papplet;
  }

  private PImage _createImage(String urlString) {
    // only normal filepaths supported for now;
    // todo; support a filepath "query" formats, like:
    // "/path/to/file.png?resize=100x100&filter=grayscale"
    // "/path/to/file.jpg#searchResultThumbnail"

    if(papplet == null){
      logger.warning("no papplet, can't load image: " + urlString);
      return null;
    }

    String filePath = urlString;
    Integer resizeWidth = null;
    Integer resizeHeight = null;

    // check for presence of query in urlString (/path/to/file?part=after&question=mark&isthe=query)
    if(urlString.contains("?"))
    {
      String[] parts = urlString.split("\\?");
      filePath = parts[0];
      String query = parts.length > 1 ? parts[1] : "";

      for(String pair : query.split("&")){
        if(pair.split("=")[0].equals("resize")){
          String resize = pair.split("=")[1];

          resizeWidth = Integer.parseInt(resize.split("x")[0]);

          if(resize.split("x").length == 2){
            resizeHeight = Integer.parseInt(resize.split("x")[1]);
          }
        }
      }
    }

    filePath = filePath.replace("\\", File.separator);

    File f = new File(filePath);
    if(!f.exists()){
      logger.warning("image file doesn't exist: "+filePath);
      return null;
    }

    logger.info("Loading image file path: "+filePath);

    PImage newImg;
    try{
      newImg = papplet.loadImage(filePath);
    } catch(java.lang.IllegalArgumentException exc){
      newImg = null;
    } catch(Exception exc){
      newImg = null;
    }

    if(newImg == null)
      return null;

    if(resizeWidth != null && resizeHeight != null){
      logger.fine("resizing image to: "+Integer.toString(resizeWidth)+"x"+Integer.toString(resizeHeight));
      newImg.resize(resizeWidth, resizeHeight);
    }


    // return operation result
    return newImg;
  }

  private PImage _createImage(Movie movie){
    if(movie == null){
      logger.warning("movie is null");
      return null;
    }

    if(papplet == null){
      logger.warning("no papplet, can't load video preview");
      return null;
    }

    //if(movie.playbin != null && !movie.playbin.isPlaying()){
    movie.play();
    movie.volume(0f);

    movie.pause();
    movie.jump(movie.duration()*0.5f);
    movie.play();
    movie.pause();

    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    //  }

    PImage img = papplet.createImage(movie.width, movie.height, PApplet.ARGB);
    img.copy(movie, 0, 0, movie.width, movie.height, 0, 0, movie.width, movie.height);
  	return img;
  }
}
