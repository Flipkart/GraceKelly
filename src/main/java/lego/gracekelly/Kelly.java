/**
 * Copyright 2013 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        /*create an expired cache entry*/
        CacheEntry<T> cacheEntry = new CacheEntry<T>(key,value,-10);
        /*put it into the cache, because if the refresh fails now, kelly
        * will try to refresh it the next time a get is called on the entry's key*/
        put(key,cacheEntry);
        /*Try to refresh the entry for the given key*/
        get(key);
    }

    /**
     * Takes a {@link CacheEntry} as input and determines wether it has expired or not.
     * @param cacheEntry
     * @return true of the entry has expired false if it has not
     */
    private boolean cacheEntryExpired(CacheEntry cacheEntry)    {

        if (cacheEntry.getTtl()==0)
            return false;

        long entryTimeStamp = cacheEntry.getEpoch_timestamp();
        long currentTime = System.currentTimeMillis()/1000;
        long ttl = cacheEntry.getTtl();

        /* is the currentTime greater than when the CacheEntry would have expired? */
        if ((entryTimeStamp+ttl) > currentTime)
            return false;
        else
            return true;
    }

    /**
     * Executes the LoaderCallable using the executorService.
     * @param cacheEntry
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void reloadCacheEntry(CacheEntry cacheEntry) throws ExecutionException, InterruptedException {
        LoaderCallable<T> loaderCallable = new LoaderCallable<T>(this, cacheProvider, cacheLoader, cacheEntry);
        executorService.submit(loaderCallable);
    }

    /**
     * Checks whether request is in flight for refreshing a given CacheEntry
     * @param cacheEntry
     * @return true if the the request is in flight or false if the request isn't in flight.
     */
    private boolean requestInFlight(CacheEntry<T> cacheEntry){
        return requestsInFlight.containsKey(cacheEntry.getKey());
    }

    /**
     * Puts a request in flight for refreshing a given CacheEntry
     * at any given time, no more than one request should be in flight
     * to refresh a CacheEntry
     * @param cacheEntry
     */
    private void putRequestInFlight(CacheEntry<T> cacheEntry){
        requestsInFlight.put(cacheEntry.getKey(),true);
    }

    /**
     * removes a request from being in flight to allow a subsequent request for
     * the same key to be put in flight.
     * @param cacheEntry
     */
    protected void removeRequestInFlight(CacheEntry<T> cacheEntry){
        synchronized (requestsInFlight) {
            requestsInFlight.remove(cacheEntry.getKey());
        }

    }
}
