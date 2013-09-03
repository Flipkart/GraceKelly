
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
import lego.gracekelly.exceptions.CacheLoaderException;

/**
 * The CacheLoader provides a single method to reload cache, based on an existing entry in the cache.
 * @param <T>
 */
public interface CacheLoader<T> {

    /**
     * Takes a {@link String} key and an value/Object of type <T> and returns a {@link CacheEntry}<T>. The
     * implementation of this method is supposed to return the CacheEntry with the latest Value for the given key.
     * @param key
     * @param prevValue
     * @return {@link CacheEntry} of the type parameter specified during declaration of this instance of CacheLoader
     * @throws CacheLoaderException
     */
    public CacheEntry<T> reload(String key, T prevValue) throws CacheLoaderException;

}
