package com.fuse.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.Ignore;

import processing.video.Movie;

// import com.fuse.cms.ModelBase;
// import com.fuse.utils.Event;

public class MovieSourceTest {
  @Test public void testNoApplet(){
    MovieSource src = new MovieSource();
    assertEquals(src.getSync("/path/doesnt/matter/here.mp4"), null);
  }

  @Ignore @Test public void test_getSync(){
    MovieSource src = new MovieSource(/* TODO: get active PApplet instance */);
    Movie mov = src.getSync("/path/doesnt/matter/here.mp4");
    assertNotNull(mov);
  }
}
