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

import lego.gracekelly.entities.CacheEntry;
import org.testng.annotations.Test;

public class CacheEntryTest {

    @Test
    public void cacheEntryWithTTL(){
        CacheEntry  cacheEntry = new CacheEntry("dude","suman",300);

        assert cacheEntry.getValue().equals("suman");
        assert cacheEntry.getKey().equals("dude");
        assert cacheEntry.getTtl() == 300;
        assert cacheEntry.getEpoch_timestamp()>0;
    }

    @Test
    public void cacheEntryWithoutTTL(){
        CacheEntry  cacheEntry = new CacheEntry("dude","suman");
        assert cacheEntry.getValue().equals("suman");
        assert cacheEntry.getKey().equals("dude");
        assert cacheEntry.getTtl() == 0;
        assert cacheEntry.getEpoch_timestamp()==0;
    }

}
