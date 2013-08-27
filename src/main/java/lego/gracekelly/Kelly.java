package lego.gracekelly;

import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;
import lego.gracekelly.exceptions.KellyException;

import java.util.concurrent.*;

/**
 * Kelly is the primary class for Gracekelly that reloads cacheEntries when they expire
 * @param <T>
 */
public class Kelly<T>{

    private final CacheLoader<T> cacheLoader;
    private final CacheProvider<T> cacheProvider;
    private final ExecutorService executorService;
    private final ConcurrentMap<String,Boolean> requestsInFlight;

    /**
     * The Kelly constructor takes a {@link CacheProvider}, {@link CacheLoader} and a threadpool size for cache reloading.
     * @param cacheProvider
     * @param cacheLoader
     * @param executorPoolSize
     */
    public Kelly(CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, int executorPoolSize){
        this.cacheProvider = cacheProvider;
        this.cacheLoader = cacheLoader;
        executorService = Executors.newFixedThreadPool(executorPoolSize);
        this.requestsInFlight = new ConcurrentHashMap<String, Boolean>();
    }

    /**
     * Get's the {@link CacheEntry} from {@link CacheProvider}, if {@link CacheEntry} is not present then returns null.
     * If the {@link CacheEntry} has expired, then a reload task is triggered through {@link CacheLoader} to reload the
     * cache with the latest value for the key.
     * @param key
     * @return null if {@link CacheEntry} was not found by {@link CacheProvider}, else returns value.
     * @throws KellyException
     */
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

    /**
     * Put's the {@link CacheEntry} into the cache provided by {@link CacheProvider}
     * @param key
     * @param value
     * @return true or false based on the success or failure of the operation
     * @throws KellyException
     */
    public boolean put(String key, CacheEntry<T> value) throws KellyException {
        try {
            return cacheProvider.put(key,value);
        } catch (CacheProviderException e) {
            throw new KellyException(e);
        }
    }

    /**
     * Expires the {@link CacheEntry} in the cache and makes a best effort to update the {@link CacheEntry} through
     * {@link CacheLoader}
     * @param key
     * @throws KellyException
     */
    public void expire(String key) throws KellyException {
        T value = get(key);
        CacheEntry<T> cacheEntry = new CacheEntry<T>(key,value,-10);//expired cache entry
        put(key,cacheEntry);
        get(key);
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


    protected void removeRequestInFlight(CacheEntry<T> cacheEntry){
        synchronized (requestsInFlight) {
            requestsInFlight.remove(cacheEntry.getKey());
        }

    }
}
