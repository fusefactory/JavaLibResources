package com.fuse.resources;

import java.util.function.Function;
import java.util.logging.Logger;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.HashMap;

import com.fuse.cms.AsyncFacade;
import com.fuse.cms.AsyncOperation;

import processing.core.PImage;

public class BaseResourceSource<K,V> {
  protected Logger logger;
  protected AsyncFacade<K,V> asyncFacade;

  private Map<K, WeakReference<V>> cache = null;
  private boolean bCacheEnabled = false;

  public BaseResourceSource(){
    logger = Logger.getLogger(BaseResourceSource.class.getName());
    asyncFacade = new AsyncFacade<>();
  }

  // fetch operations // // // // //

  public AsyncFacade<K,V> getAsyncFacade(){
    return asyncFacade;
  }

  public AsyncOperation<V> getAsync(K key){
    V cachedItem = getCache(key);

    if(cachedItem != null){
      this.logger.info("cache-hit: "+key);
      AsyncOperation<V> op = new AsyncOperation<>();
      op.add(cachedItem);
      op.finish(true); // success!
      return op;
    }

    AsyncOperation<V> op = asyncFacade.getAsync(key);

    // cache if enabled
    op.withSingleResult((V item) -> {
      if(this.bCacheEnabled)
        this.setCache(key,  item);
    });

    return op;
  }

  public V getSync(K key){
    V item = getCache(key);

    if(item != null){
      return item;
    }

    item = asyncFacade.getSync(key);

    if(item != null && this.bCacheEnabled)
      this.setCache(key,  item);

    return item;
  }

  // configuration methods // // // // //

  public void setLoader(Function<K, V> func){
    // The async loader is used when the getAsync method is called,
    // the sync loader is used when the getSync method is called.
    asyncFacade.setSyncLoader((K key) -> {
      V cachedItem = getCache(key);
      if(cachedItem != null)
        return cachedItem;

      V item = func.apply(key);

      if(item != null && this.bCacheEnabled)
        this.setCache(key, item);

      return item;
    });
  }

  public void setCacheEnabled(boolean enabled){
    bCacheEnabled = enabled;
  }

  /**
   * Tries to remove item from cache but falls back to general item memory cleanup also if not found in cache.
   * @param item to remove and cleanup
   */
  public void remove(V item) {
    // when clearing cache, clearItem is automatically called.
    // Otherwise we'll call it  manually
    if(!this.removeFromCache(item)) {
      this.clearItem(item);
    }
  }

  // caching methods // // // // //

  protected void setCache(K key, V item){
    // remove any existing cache for this key
    this.clearCache(key);

    // our cache container is lazy-initialized to save memory when caching is not enabled
    if(cache == null)
      cache = new HashMap<K, WeakReference<V>>();

    // write to cache container
    cache.put(key, new WeakReference<V>(item));
  }

  // get item from cache by key/identifier
  protected V getCache(K key){
    if(cache == null)
      return null;

    WeakReference<V> cachedRef = cache.get(key);

    if(cachedRef == null)
      return null;

    // logger.finer("RESOURCE CACHE-HIT: "+key.toString());

    if(cachedRef.get() == null){
      logger.info("cache-expired: "+key.toString());
      cache.remove(key);
      return null;
    }

    return cachedRef.get();
  }

  // remove an item from cache by key/identifier
  protected boolean clearCache(K key){
    WeakReference<V> removedRef = this.cache == null ? null : this.cache.remove(key);

    if(removedRef == null) 
      return false;

    V item = removedRef.get();
    if(item != null)
      this.clearItem(item);

    this.logger.info("cleared-cache-key: "+key);
    return true;
  }

  // remove an item from cache by value (find corresponding key/identifier and calls this.clearCache)
  protected boolean removeFromCache(V value) {
    if(this.cache == null)
      return false; // no existing cache; nothing to remove


    K key = null;
    Object[] keyArray = this.cache.keySet().toArray();

    for(int i=keyArray.length-1; i>=0; i--) {
    	K curKey = (K)keyArray[i];
        if(this.getCache(curKey) == value) {
        	key = curKey;
        	break;
        }
    }

    return key != null && this.clearCache(key);
  }

  // perform any additional memory cleanup for the removed item
  protected void clearItem(V item) {
    // nothing to do by default, inheriting classes can overwrite this method
  }
}
