package lego.gracekelly.api;

import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;

import java.util.concurrent.Future;

/**
 * The CacheProvider interface is used to implement adapters to different cache implementations where the cached values
 * are finally persisted and retrieved from. For eg: one would implement a CacheProvider for couchbase or memcached.
 */
public interface CacheProvider <T>{
    /**
     * Returns a {@link CacheEntry}<T> if it is present in the underlying cache, or it returns a null otherwise.
     * @param key
     * @return {@link CacheEntry}<T> for the given key or return null if not present
     * @throws CacheProviderException
     */
    CacheEntry<T> get(String key) throws CacheProviderException;

    /**
     * Tries to update the cache with the given {@link CacheEntry}<T> for the given key
     * @param key
     * @param value
     * @return true or false based on the success of putting the {@link CacheEntry}<T> into the cache.
     * @throws CacheProviderException
     */
    Boolean put(String key, CacheEntry<T> value) throws CacheProviderException;
}
