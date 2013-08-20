package lego.gracekelly.api;


import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheLoaderException;


public interface CacheLoader<T> {

    public CacheEntry<T> reload(String key, T prevValue) throws CacheLoaderException;

    public CacheEntry<T> load(String key) throws CacheLoaderException;
}
