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

package lego.gracekelly.examples.basic;


import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple {@link CacheProvider} that uses a ConcurrentHashMap to provide
 * a cache implementation for Kelly
 */
public class LocalCacheProvider implements CacheProvider<String>{

    private final Map<String,CacheEntry<String>> cache = new ConcurrentHashMap<String,CacheEntry<String>>();

    /**
     * returns the {@link CacheEntry<String>} stored against a given key
     * @param key
     * @return {@link CacheEntry} with the appropriate value
     * @throws CacheProviderException
     */
    @Override
    public CacheEntry<String> get(String key) throws CacheProviderException {
        return cache.get("key");
    }

    /**
     * puts the given {@link CacheEntry<String>} against the given key in the underlying cache
     * @param key
     * @param value
     * @return true
     * @throws CacheProviderException
     */
    @Override
    public Boolean put(String key, CacheEntry<String> value) throws CacheProviderException {
        cache.put(key,value);
        return true;
    }
}
