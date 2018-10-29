package com.fuse.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.Ignore;

import com.fuse.vlcplayer.VLCPlayer;

public class VlcPlayerSourceTest {

  @Ignore @Test public void test_getSync(){
    VlcPlayerSource src = new VlcPlayerSource();
    VLCPlayer player = src.getSync("testdata/vid.mp4");
    assertNotNull(player);
  }
}
