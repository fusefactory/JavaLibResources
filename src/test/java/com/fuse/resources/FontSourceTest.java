package com.fuse.resources;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import com.fuse.cms.ModelBase;
import com.fuse.utils.Event;

public class FontSourceTest {
  @Test public void testNoApplet(){
    FontSource src = new FontSource();
    assertEquals(src.getSync("/path/doesnt/matter/here.ttf"), null);
  }
}
