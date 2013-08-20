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
     * Get's the relevant object from the cache provider synchronously. If something goes wrong a
     * {@link CacheProviderException} is thrown.
     * @param key
     * @return Object that was persisted in the cache for the given key, or null if the value is unavailable
     * @throws CacheProviderException
     */
    CacheEntry<T> get(String key) throws CacheProviderException;

    /**
     * Asynchronously puts the Object into the cache along with the given key. The Future boolean represents
     * whether the operation was successful or not.
     * @param key
     * @param value
     * @return Future of a Boolean that will eventually hold a true/false or will throw an exception of future.get
     * @throws CacheProviderException
     */
    Boolean put(String key, CacheEntry<T> value) throws CacheProviderException;
}
