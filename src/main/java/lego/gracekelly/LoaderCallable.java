package lego.gracekelly;


import lego.gracekelly.Kelly;
import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;

import java.util.concurrent.Callable;

public class LoaderCallable<T> implements Callable {

    private final CacheLoader<T> cacheLoader;
    private final CacheProvider<T> cacheProvider;
    private final CacheEntry<T> cacheEntry;
    private final Kelly<T> kelly;

    public LoaderCallable(Kelly<T> kelly, CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, CacheEntry<T> cacheEntry){
        this.cacheLoader = cacheLoader;
        this.cacheProvider = cacheProvider;
        this.cacheEntry = cacheEntry;
        this.kelly = kelly;
    }

    @Override
    public Object call() throws Exception {
        CacheEntry<T> cacheEntryTemp = cacheLoader.reload(cacheEntry.getKey(), cacheEntry.getValue());
        Boolean status = cacheProvider.put(cacheEntryTemp.getKey(), cacheEntryTemp);
        kelly.removeRequestInFlight(cacheEntry);
        return status;
    }
}