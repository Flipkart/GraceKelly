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

package lego.gracekelly.cacheproviders.highlevel;

import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;

/**
 * A {@link FallbackCacheProvider} is a higher level cache provider than combines
 * two different cache providers where one acts as an L1 and primary cache, while another
 * acts as a fallback cache.
 * @param <T>
 */
public class FallbackCacheProvider<T> implements CacheProvider<T> {

    private final CacheProvider<T> primaryCache;
    private final CacheProvider<T> fallbackCache;

    /**
     * The constructor takes two {@link CacheProvider}s to instantiate.
     * One is primary and another is a fallback cache provider
     * @param primaryCache
     * @param fallbackCache
     */
    public FallbackCacheProvider(CacheProvider<T> primaryCache, CacheProvider<T> fallbackCache) {
        this.primaryCache = primaryCache;
        this.fallbackCache = fallbackCache;
    }

    /**
     * Attempts to get the value for a key from the primary cache. If the value is not present
     * in the primary cache, it attempts to get the value from the fallback cache.
     * @param key
     * @return value of present, else null
     * @throws CacheProviderException
     */
    @Override
    public CacheEntry<T> get(String key) throws CacheProviderException {
        CacheEntry<T> cacheEntry;
        cacheEntry = primaryCache.get(key);
        if (cacheEntry==null){
            cacheEntry = fallbackCache.get(key);
        }
        return cacheEntry;
    }

    /**
     * While putting values in cache, the values are put in both primary and fallback caches.
     * @param key
     * @param value
     * @return true if setting in primary was a success, else returns false.
     * @throws CacheProviderException
     */
    @Override
    public Boolean put(String key, CacheEntry<T> value) throws CacheProviderException {
        boolean primaryPut = false;
        primaryPut = primaryCache.put(key,value);
        fallbackCache.put(key,value);
        return primaryPut;
    }

}
