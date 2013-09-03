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

import java.util.concurrent.Callable;

/**
 * The callable that is used by {@link Kelly} to reload the {@link CacheProvider} with a {@link CacheEntry}
 * using the provided {@link CacheLoader}
 * @param <T>
 */
public class LoaderCallable<T> implements Callable {

    private final CacheLoader<T> cacheLoader;
    private final CacheProvider<T> cacheProvider;
    private final CacheEntry<T> cacheEntry;
    private final Kelly<T> kelly;

    public LoaderCallable(Kelly<T> kelly, CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, CacheEntry<T> cacheEntry){
        this.cacheLoader = cacheLoader;
        this.cacheProvider = cacheProvider;
        this.cacheEntry = cacheEntry;
        this.kelly = kelly;
    }

    @Override
    public Object call() throws Exception {
        CacheEntry<T> cacheEntryTemp = cacheLoader.reload(cacheEntry.getKey(), cacheEntry.getValue());
        Boolean status = cacheProvider.put(cacheEntryTemp.getKey(), cacheEntryTemp);
        kelly.removeRequestInFlight(cacheEntry);
        return status;
    }
}