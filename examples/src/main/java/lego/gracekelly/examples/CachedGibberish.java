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

package lego.gracekelly.examples;

import lego.gracekelly.Kelly;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.KellyException;

public class CachedGibberish {

    private final Kelly<String> gibberishCache;

    public CachedGibberish(){
        /* A new kelly instance is created with LocalCacheProvider,  GibberishCacheLoader and a
        executorService pool size of 1*/
        gibberishCache = new Kelly<String>(new LocalCacheProvider(),new GibberishCacheLoader(), 1);
    }

    public String getGibberish(String prefix){

        String gibberishValue = null;

        /* tries to get the cached value for a given prefix */
        try {
            /*if the value is not present in the cache, it returns null. If the value is present
            * in cache but has expired, the value is still returned but a background task is dispatched
            * to refresh the cache with the latest value. If successful it should be available for subsequent
            * get's
            * */
            gibberishValue = gibberishCache.get(prefix);
        } catch (KellyException e) {
            //do nothing;
        }

        if (gibberishValue == null){
            /*get gibberish value from GibberishService*/
            gibberishValue = GibberishService.getGibberish(prefix);
            try {
                /* put the value in */
                gibberishCache.put(prefix, new CacheEntry<String>(prefix, gibberishValue, 300));
            } catch (KellyException e) {
                e.printStackTrace();
            }
        }
        return gibberishValue;
    }
}
