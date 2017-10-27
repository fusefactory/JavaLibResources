package com.fuse.resources;

import java.util.function.Function;
import java.util.logging.Logger;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.HashMap;

import com.fuse.cms.AsyncFacade;
import com.fuse.cms.AsyncOperation;

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

  // caching methods // // // // //

  protected void setCache(K key, V item){
    if(cache == null)
      cache = new HashMap<K, WeakReference<V>>();

    cache.put(key, new WeakReference<V>(item));
  }

  protected V getCache(K key){
    if(cache== null)
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

  protected boolean clearCache(K key){
    this.logger.info("clear-cache: "+key);
    return this.cache == null ? null : this.cache.remove(key) != null;
  }

  protected boolean removeFromCache(V value) {
    K key = null;
    for(K curKey : this.cache.keySet()) {
      if(this.getCache(curKey) == value) {
        key = curKey;
        break;
      }
    }

    return key != null && this.clearCache(key);
  }
}
