/**
 * Copyright 2014 Flipkart Internet, pvt ltd.
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

package lego.gracekelly.cacheproviders.memcached;


import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A cache provider for a single memcached instance
 * @param <T>
 */
public class MemcachedProvider<T> implements CacheProvider<T> {

    private final MemcachedClient memcachedClient;
    private final static int INFINITE_TTL = 0;
    private final int TTL;

    /**
     * Create a new memcached provider by giving it the {@link InetSocketAddress} of the memcached
     * host.
     * @param inetSocketAddress
     * @throws CacheProviderException
     */
    public MemcachedProvider(InetSocketAddress inetSocketAddress, final int TTL) throws CacheProviderException {
        try {
            memcachedClient = new MemcachedClient(inetSocketAddress);
            this.TTL = TTL;
        } catch (IOException e) {
            throw new CacheProviderException(e);
        }
    }

    /**
     * Create a new memcached provider by giving it a {@link List} of {@link InetSocketAddress}s
     * @param inetSocketAddresses
     * @throws CacheProviderException
     */
    public MemcachedProvider(List<InetSocketAddress> inetSocketAddresses, final int TTL) throws CacheProviderException {
        try {
            memcachedClient = new MemcachedClient(inetSocketAddresses);
            this.TTL = TTL;
        } catch (IOException e) {
            throw new CacheProviderException(e);
        }
    }

    /**
     * Get the {@link CacheEntry} for a given key
     * @param key
     * @return a {@link CacheEntry}
     * @throws CacheProviderException
     */
    @Override
    public CacheEntry<T> get(String key) throws CacheProviderException {
        return (CacheEntry<T>) memcachedClient.get(key);
    }

    /**
     *
     * @param key
     * @param value
     * @return true if successful, false if not
     * @throws CacheProviderException
     */
    @Override
    public Boolean put(String key, CacheEntry<T> value) throws CacheProviderException {
        try {
            return memcachedClient.set(key,TTL, value).get();
        } catch (InterruptedException e) {
            throw new CacheProviderException(e);
        } catch (ExecutionException e) {
            throw new CacheProviderException(e);
        }
    }
}
