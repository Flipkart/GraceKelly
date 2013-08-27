package lego.gracekelly;

import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;
import lego.gracekelly.exceptions.KellyException;
import lego.gracekelly.helpers.LoaderCallable;

import java.util.concurrent.*;

public class Kelly<T>{

    private final CacheLoader<T> cacheLoader;
    private final CacheProvider<T> cacheProvider;
    private final ExecutorService executorService;
    private final ConcurrentMap<String,Boolean> requestsInFlight;

    public Kelly(CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, int executorPoolSize){
        this.cacheProvider = cacheProvider;
        this.cacheLoader = cacheLoader;
        executorService = Executors.newFixedThreadPool(executorPoolSize);
        this.requestsInFlight = new ConcurrentHashMap<String, Boolean>();
    }

    public T get(String key) throws KellyException {
        CacheEntry<T> cacheEntry = null;
        try {
            cacheEntry = cacheProvider.get(key);
        } catch (CacheProviderException e) {
            throw new KellyException(e);
        }

        if (cacheEntry != null){
            if(cacheEntryExpired(cacheEntry)) synchronized (requestsInFlight) {
                if(!requestInFlight(cacheEntry))
                    try {
                        putRequestInFlight(cacheEntry);
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

    public boolean put(String key, CacheEntry<T> value) throws KellyException {
        try {
            return cacheProvider.put(key,value);
        } catch (CacheProviderException e) {
            throw new KellyException(e);
        }
    }

    private boolean cacheEntryExpired(CacheEntry cacheEntry)    {

        if (cacheEntry.getTtl()==0)
            return false;

        long entryTimeStamp = cacheEntry.getEpoch_timestamp();
        long currentTime = System.currentTimeMillis()/1000;
        long ttl = cacheEntry.getTtl();

        if ((entryTimeStamp+ttl) > currentTime)
            return false;
        else
            return true;
    }

    private void reloadCacheEntry(CacheEntry cacheEntry) throws ExecutionException, InterruptedException {
        LoaderCallable<T> loaderCallable = new LoaderCallable<T>(this, cacheProvider, cacheLoader, cacheEntry);
        executorService.submit(loaderCallable);
    }

    private boolean requestInFlight(CacheEntry<T> cacheEntry){
        return requestsInFlight.containsKey(cacheEntry.getKey());
    }

    private void putRequestInFlight(CacheEntry<T> cacheEntry){
        requestsInFlight.put(cacheEntry.getKey(),true);
    }

    public void removeRequestInFlight(CacheEntry<T> cacheEntry){
        synchronized (requestsInFlight) {
            requestsInFlight.remove(cacheEntry.getKey());
        }

    }
}
