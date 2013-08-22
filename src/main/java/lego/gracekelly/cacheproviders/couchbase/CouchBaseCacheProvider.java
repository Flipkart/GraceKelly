package lego.gracekelly.cacheproviders.couchbase;


import com.couchbase.client.CouchbaseClient;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class implements {@link CacheProvider}<T> interface for Couchbase using a {@link CouchbaseClient} instance.
 * @param <T>
 */
public class CouchBaseCacheProvider<T> implements CacheProvider<T>{

    private final CouchbaseClient couchbaseClient;
    private final long timeout;

    /**
     * Constructor with timeout for cache gets
     * @param couchbaseClient
     * @param timeout
     */
    public CouchBaseCacheProvider(CouchbaseClient couchbaseClient , long timeout){
        this.couchbaseClient = couchbaseClient;
        this.timeout = timeout;
    }

    /**
     * Constructor with no timeouts for cache gets
     * @param couchbaseClient
     */
    public CouchBaseCacheProvider(CouchbaseClient couchbaseClient){
        this.couchbaseClient = couchbaseClient;
        this.timeout = 0;
    }

    /**
     * Tries the get the {@link CacheEntry}<T> value from the underlying cache, constrained by a timeout
     * if a timeout value is provided.
     * @param key
     * @return {@link CacheEntry}<T> for the given key, from the provided {@link CouchbaseClient}
     * @throws CacheProviderException
     */
    @Override
    public CacheEntry<T> get(String key) throws CacheProviderException {
        GetFuture cacheEntryGetFuture = couchbaseClient.asyncGet(key);
        try {
            CacheEntry<T> cacheEntry;
            if (timeout>0)
                cacheEntry = (CacheEntry<T>)cacheEntryGetFuture.get(timeout,TimeUnit.SECONDS);
            else
                cacheEntry = (CacheEntry<T>)cacheEntryGetFuture.get();
            return cacheEntry;
        } catch (InterruptedException e) {
            throw new CacheProviderException(e);
        } catch (ExecutionException e) {
            throw new CacheProviderException(e);
        } catch (TimeoutException e){
            throw new CacheProviderException(e);
        }
    }

    @Override
    public Boolean put(String key, CacheEntry<T> value) throws CacheProviderException {
        OperationFuture<Boolean> operationFuture = couchbaseClient.set(key, value);
        try {
            return operationFuture.get();
        } catch (InterruptedException e) {
            throw new CacheProviderException(e);
        } catch (ExecutionException e) {
            throw new CacheProviderException(e);
        }
    }
}
