package com.fuse.resources;

import processing.core.PApplet;
import processing.video.Movie;

/**
  * Movie loading interface which takes care
  * of loading from disk and caching and providing an easy
  * (both synchronous and asynchronous) interface for accessing.
  */
public class MovieSource extends BaseResourceSource<String, Movie> {
  // USAGE: load movie asynchronously from file path
  //    MovieSource.getAsync("/path/to/local/file")
  //
  //  USAGE: handling asynchronous response
  //    getAsync("/path/to/file")
  //      .withSingleResult((Movie loadedMovie) -> {
  //        // this callback is only called once for every instance
  //        // that was loaded in this operation (which should be only one per request)
  //      })
  //      .withResult((List<Movie> loadedMovies) -> {
  //        // this callback is called when an operation is finished, regardless if it
  //        // was a success, failure or aborted. The parameter is a list of all movies
  //        // that were loaded during the operation (can be empty).
  //      })
  //      .whenNoResult((AsyncOperation<Movie> op) -> {
  //        // this callback can be used to handle situations when nothing
  //        // was loaded (for example if the file didn't exist or the opertions was aborted)
  //      });

  private PApplet papplet;

  public MovieSource(){
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

  public MovieSource(PApplet papplet){
	  this();
	  this.setPapplet(papplet);
  }

  public void setPapplet(PApplet papplet){
    this.papplet = papplet;
  }

  /** The main loader routine */
  private Movie _load(String url){
    // this method is called by the loader which is registered
    // in the constructor method

    // the 'url' can be normal a file path, like;
    //    /tmp/video.mp4 (absolute)
    //    data/video.mov (relative)

    // returning null implies a failure to laod the video
    if(papplet == null){
      logger.warning("no papplet, can't load movie: " + url);
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
    logger.fine("Loading movie: "+url);
    Movie newMovie = null;

    try{

      newMovie = new Movie(this.papplet, filePath);

    } catch(java.lang.UnsatisfiedLinkError exc){
      this.logger.warning("Failed to load movie '"+ filePath+"', probably GStreamer binaries aren't properly loaded:\n\n");
      this.logger.warning(exc.getMessage());
      exc.printStackTrace();
      this.logger.warning("\n\n");
      newMovie = null;
    } catch(Exception exc){
      this.logger.warning("Failed to load movie '"+ filePath+"':\n\n");
      this.logger.warning(exc.getMessage());
      exc.printStackTrace();
      this.logger.warning("\n\n");
      newMovie = null;
    } catch(java.lang.NoClassDefFoundError err) {
      this.logger.warning("Failed to load movie '"+ filePath+"':\n\n");
      this.logger.warning(err.getMessage());
      err.printStackTrace();
      this.logger.warning("\n\n");
      newMovie = null;
    }

    return newMovie;
  }
}
