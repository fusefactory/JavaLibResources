package com.fuse.resources;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import com.fuse.cms.ModelBase;
import com.fuse.utils.Event;

public class BaseResourceSourceTest {
  @Test public void testCashConcurrentModException(){

    class Tmp extends BaseResourceSource<String,Integer> {

      public int triggerRaceConditions(){
        final List<Exception> exceptions = new ArrayList<>();

        Thread t1 = new Thread(() -> {
          int i;
          for(i=0; i<1000; i+=1) {
            Integer ii = i;
            super.setCache(Integer.toString(i), ii);
          }
        });

        Thread t2 = new Thread(() -> {
          int i;
          for(i=0; i<1000; i+=1) {
            Integer ii = i;
            try {
              super.removeFromCache(ii);
            } catch (java.util.ConcurrentModificationException exc) {
              exceptions.add(exc);
            }
          }
        });

        t1.start();
        t2.start();

        try {
          t1.join();
          t2.join();
        } catch(java.lang.InterruptedException exc) {
          exceptions.add(exc);
        }

        return exceptions.size();
      }
    }

    Tmp tmp = new Tmp();
    assertEquals(tmp.triggerRaceConditions(), 0);
  }
}
