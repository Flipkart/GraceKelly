/**
 * Copyright 2013 Flipkart, Inc.
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
