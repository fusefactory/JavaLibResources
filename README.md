# JavaLibResources

[![Build Status](https://travis-ci.org/fusefactory/JavaLibResources.svg?branch=master)](https://travis-ci.org/fusefactory/JavaLibResources)

Resource management classes.

## JavaDocs
https://fusefactory.github.io/JavaLibResources/site/apidocs/index.html

## ImageSource
ImageSource is a BaseResourceSource which takes a string and provides PImage (the processing framework's image class) instances. It requires a PApplet instance in its constructor or to be later set using the setPapplet method.

```java
import com.fuse.resource.ImageSource;
ImageSource imageSource = new ImageSource(papplet);
```

you can load images by file path;

```java
PImage img = imageSource.getSync("path/to/file.jpg");
```

and you can have the image be automatically resized;

```java
PImage img = imageSource.getSync("path/to/file.jpg?resize=200x100");
```

these things work asynchornously as well of course;

```java
imageSource.getAsync("path/to/file.jpg")
  .withSingleResult((PImage img) -> {
    // do somthing with the image here
  });

imageSource.getAsync("path/to/file.jpg?resize=200x100")
  .withSingleResult((PImage img) -> {
    // do somthing with the image here
  });
```


## Create your own custom resource source

This package provides a BaseResourceSource class which provides structure for requesting resources both synchronous and asynchronous and can optionally take care of caching as well.

```java

import com.fuse.resources.BaseResourceSource;

// we'll create a custom ResourceSource which takes identifiers of the type Long and
// provides instances of the resource "MyCustomResource"
class MyCustomSource extends BaseResourceSource<Long, MyCustomResource> {
  private Database db;

  public MyCustomSource(Database db){
    this.db = db;

    // specify whether or not this custom source should perform caching
    super.setCacheEnabled(true);

    // configure our custom resource loader; it takes an identifer (which in the
    // case of this resource source is a String) and returns an instance of MyCustomResource
    setLoader((Long id) -> {
      // maybe you're resource-source will take care of fetching data from a backend?
      DatabaseRecord data = this.db.fetchByIdFrom("MyCustomResource", id);
      // and turn the database data into an instance...
      MyCustomResource instance = new MyCustomResource(data);
      // This loader simply returns the instance and BaseResourceSource will take it trom there
      // the returned value can also be null, which will result in a AsyncOperation failure for
      // asynchronous requests).
      return instance;
    });
  }
}
```

with the above implementation you can do asynchronous (threaded, by default) fetching;

```java
  MyCustomSource src = new MyCustomSource();

  src.getAsync(10457).
    withSingleResult((MyCustomResource instance) -> {
      // do something with loaded instance;
    });
```

you can also request instances synchronously (blocking);

```java
  MyCustomSource src = new MyCustomSource();
  MyCustomResource instance = src.getSync(80887);
```

For all the sync/async logic the ResourceSource uses an AsyncFacade form the com.fuse.cms package.
You can get a referemce to its asyncfacade using the getAsyncFacade method:

```java
  MyCustomSource src = new MyCustomSource();
  src.getAsyncFacade().getRecycleActiveOperations(false);
```
