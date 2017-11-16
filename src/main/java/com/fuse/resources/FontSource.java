package com.fuse.resources;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import processing.core.PApplet;
import processing.core.PFont;

/**
  * Font loading interface which takes care
  * of loading from disk and caching and providing an easy
  * (both synchronous and asynchronous) interface for accessing.
  */
public class FontSource extends BaseResourceSource<String, PFont> {
  // USAGE: load font asynchronously from file path
  //    fontSource.getAsync("/path/to/local/file")
  //
  // USAGE: load font async with size parameter
  //    fontCollectionInstance.getAsync("/path/to/local/file?size=35")
  //
  //  USAGE: handling asynchronous response
  //    getAsync("/path/to/font")
  //      .withSingleResult((PFont loadedFont) -> {
  //        // this callback is only called once for every font instance
  //        // that was loaded in this operation (which should be only one per request)
  //      })
  //      .withResult((List<PFont> loadedFonts) -> {
  //        // this callback is called when an operation is finished, regardless if it
  //        // was a success, failure or aborted. The parameter is a list of all fonts
  //        // that were loaded during the operation (can be empty).
  //      })
  //      .whenNoResult((AsyncOperation<PFont> op) -> {
  //        // this callback can be used to handle situations when nothing
  //        // was loaded (for example if the file didn't exist or the opertions was aborted)
  //      });

  private PApplet papplet;

  public FontSource(){
    super.setCacheEnabled(true);

    // here we register the collection's loader logic, which is called when
    // an item is requested that is not cached.
    // This synchronous loader will automatically be converted into a threaded asynchronous
    // loader by the asyncFacade class. The sync loader will be used when the getSync
    // method is called, the async loader will be used when the getAsync method is called.
    setLoader((String url) -> {
      return this._load(url);
    });
  }

  public FontSource(PApplet papplet){
	  this();
	  this.setPapplet(papplet);
  }

  public void setPapplet(PApplet papplet){
    this.papplet = papplet;
  }

  /** The main loader routine */
  private PFont _load(String url){
    // this method is called by the loader which is registered in the constructor method

    // the 'url' can be normal a file path, like;
    //    /tmp/font.ttf (absolute)
    //    data/fonts/font.ttf (relative)

    // the url may also contains url-format query parameters.
    // Currently the size parameter is supported;
    //    data/fonts/font.ttf?size=24

    if(papplet == null){
      logger.warning("no papplet, can't load font: " + url);
      return null;
    }

    String filePath = url;

    String query = null;
    if(url.contains("?")){
      String[] parts = filePath.split("\\?");
      filePath = parts[0];
      query = parts[1];
    }

    int size = 20;
    if(query != null){
      Pattern p = Pattern.compile("size=(\\d+)");
      Matcher m = p.matcher(query);
      if(m.find())
        size = Integer.parseInt(m.group(1));
    }

    logger.fine("Loading font: "+url);
    PFont newItem = papplet.createFont(filePath, size, true);

    return newItem;
  }
}
