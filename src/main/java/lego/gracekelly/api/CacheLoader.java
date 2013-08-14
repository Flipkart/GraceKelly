package lego.gracekelly.api;


import lego.gracekelly.entities.CacheEntry;

import java.util.concurrent.Callable;

/**
 * The CacheLoader is an abstract class that implements a callable. The CacheLoader takes a CacheEntry as a constructor
 * argument whose key/value can be used by the callable to process and return the appropriate(latest) CacheEntry.
 * @param <T>
 */
public abstract class CacheLoader<T> implements Callable<CacheEntry<T>> {

    private final CacheEntry<T> cacheEntry;

    private CacheLoader() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }

    public CacheLoader(CacheEntry<T> cacheEntry){
        this.cacheEntry = cacheEntry;
    }

}
