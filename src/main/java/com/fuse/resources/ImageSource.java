package com.fuse.resources;

import java.io.File;
import processing.opengl.Texture;
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
    super.asyncFacade.setThreadPriority(Thread.MIN_PRIORITY);

    // register our load method to be used when images are requested
    // this loader is used both for sync and async requests
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
    // "/path/to/file.png?resize=100x100 // resizes image to exactly specified dimensions
    // "/path/to/file.png?fillSize=1000x800 // downscales image to fill specified dimensions (respecting aspect ratio)
    // "/path/to/file.png?cache=true // forces caching of this url, even when caching is disabled

    if(papplet == null){
      logger.warning("no papplet, can't load image: " + urlString);
      return null;
    }

    String filePath = urlString;
    Integer resizeWidth = null;
    Integer resizeHeight = null;
    Boolean doCache = null;
    int[] fillSize = null;

    // check for presence of query in urlString (/path/to/file?part=after&question=mark&isthe=query)
    if(urlString.contains("?"))
    {
      String[] parts = urlString.split("\\?");
      filePath = parts[0];
      String query = parts.length > 1 ? parts[1] : "";

      // loop over each param/value pair in the query part of the urlString
      for(String pair : query.split("&")){
        if(pair.split("=")[0].equals("resize")){
          String resize = pair.split("=")[1];

          resizeWidth = Integer.parseInt(resize.split("x")[0]);

          if(resize.split("x").length == 2){
            resizeHeight = Integer.parseInt(resize.split("x")[1]);
          }
        }

        if(pair.split("=")[0].equals("fillSize")){
          String[] val = pair.split("=")[1].split("x");
          fillSize = new int[2];
          fillSize[0] = Integer.parseInt(val[0]);
          fillSize[1] = Integer.parseInt(val.length > 1 ? val[1] : val[0]);
        }

        if(pair.split("=")[0].equals("cache")){
          doCache = Boolean.parseBoolean(pair.split("=")[1]);
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

    if(fillSize != null){
      float x = (float)fillSize[0] / (float)newImg.width;
      float y = (float)fillSize[1] / (float)newImg.height;
      float factor = Math.max(x,y);
      resizeWidth = (int)Math.ceil(newImg.width * factor);
      resizeHeight = (int)Math.ceil(newImg.height * factor);
    }

    if(resizeWidth != null && resizeHeight != null){
      logger.info("resizing image to: "+Integer.toString(resizeWidth)+"x"+Integer.toString(resizeHeight));
      newImg.resize(resizeWidth, resizeHeight);
    }

    // if cache is enabled, caching will be taken care of by theparent class (BaseResourceSource)
    // otherwise, if caching is explicitly request for this image, we'll set cache here
    if(doCache != null && doCache == true && !this.isCacheEnabled()) {
      System.out.println("explicit-cache: "+urlString);
      this.setCache(urlString,  newImg);
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

  @Deprecated // use remove
  public void destroy(PImage img) {
    super.remove(img);
  }

  @Override
  protected void clearItem(PImage img) {
    // System.out.println("clear-item");

    if(this.papplet != null) {
      Object cache = this.papplet.getGraphics().getCache(img);
      if(cache  instanceof Texture) {
        Texture tex = (Texture)cache;
        tex.unbind();
        tex.disposeSourceBuffer();
      }

      // if(cache != null)
      //   System.out.println("removedCache on papplet for: "+cache.toString());

      this.papplet.getGraphics().removeCache(img);
    }

    // explicitlty request garbage collecting?
    // System.gc();
  }
}
