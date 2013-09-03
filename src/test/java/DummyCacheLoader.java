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

import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheLoaderException;


public class DummyCacheLoader implements CacheLoader<String>{
        public static Integer invocations = 0;

        @Override
        public CacheEntry<String> reload(String key, String prevValue) throws CacheLoaderException {
            invocations++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new CacheEntry<String>("dude","suman karthik",1);  //To change body of implemented methods use File | Settings | File Templates.
        }
}
