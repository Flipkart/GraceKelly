package lego.gracekelly.cacheproviders.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;

public class GuavaCacheProvider<T> implements CacheProvider<T> {

  LoadingCache<String, CacheEntry<T>> cache;
  long maximumSize = 1000;

  public GuavaCacheProvider() {
    new GuavaCacheProvider(maximumSize);
  }

  public GuavaCacheProvider(long maximumSize) {
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(maximumSize)
        .build(
            new CacheLoader<String, CacheEntry<T>>() {
              @Override
              public CacheEntry<T> load(String s) throws Exception {
                return null;
              }
            }
        );
  }

  @Override
  public CacheEntry<T> get(String key) throws CacheProviderException {
    try {
      return cache.get(key);
    } catch (Exception e) {
      return null;
    } catch (Error e) {
      return null;
    }
  }

  @Override
  public Boolean put(String key, CacheEntry<T> cacheEntry) throws CacheProviderException {
    cache.put(key, cacheEntry);
    return true;
  }
}
