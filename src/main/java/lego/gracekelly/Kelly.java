package lego.gracekelly;

import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;
import lego.gracekelly.exceptions.KellyException;
import lego.gracekelly.helpers.LoaderCallable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Kelly<T>{

    private final CacheLoader<T> cacheLoader;
    private final CacheProvider<T> cacheProvider;
    private final ExecutorService executorService;

    public Kelly(CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, int executorPoolSize){
        this.cacheProvider = cacheProvider;
        this.cacheLoader = cacheLoader;
        executorService = Executors.newFixedThreadPool(executorPoolSize);
    }

    public T get(String key) throws KellyException {

        CacheEntry<T> cacheEntry = null;
        try {
            cacheEntry = cacheProvider.get(key);
        } catch (CacheProviderException e) {
            throw new KellyException(e);
        }

        if (cacheEntry != null){
            if(cacheEntryExpired(cacheEntry)){
                try {
                    reloadCacheEntry(cacheEntry);
                } catch (ExecutionException e) {
                    throw new KellyException(e);
                } catch (InterruptedException e) {
                    throw new KellyException(e);
                }
            }

            return cacheEntry.getValue();
        }
        else {
            return null;
        }
    }

    private boolean cacheEntryExpired(CacheEntry cacheEntry){

        if (cacheEntry.getTtl()==0)
            return false;

        long entryTimeStamp = cacheEntry.getEpoch_timestamp();
        long currentTime = System.currentTimeMillis()/1000;
        long ttl = cacheEntry.getTtl();
        if ((entryTimeStamp+ttl) < currentTime)
            return true;
        else
            return false;
    }

    private void reloadCacheEntry(CacheEntry cacheEntry) throws ExecutionException, InterruptedException {
        LoaderCallable<T> loaderCallable = new LoaderCallable<T>(cacheProvider, cacheLoader, cacheEntry);
        executorService.submit(loaderCallable);
    }
}
