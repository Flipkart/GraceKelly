package lego.gracekelly; /**
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
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheLoaderException;
import lego.gracekelly.exceptions.CacheProviderException;
import lego.gracekelly.exceptions.KellyException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class KellyTest {

    @Test
    public void kellyExceptionsTest() throws CacheProviderException {

        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = Mockito.mock(CacheLoader.class);
        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude","suman",50);

        boolean kellyExceptionThrown = false;

        Mockito.when(cacheProvider.get("dude")).thenThrow(new CacheProviderException("exception"));
        Mockito.when(cacheProvider.put("dude",cacheEntry)).thenThrow(new CacheProviderException("exception"));

        Kelly kelly = new Kelly(cacheProvider, cacheLoader, 1);

        try {
            kelly.get("dude");
        } catch (KellyException e) {
            kellyExceptionThrown = true;
        }

        assert kellyExceptionThrown;

        kellyExceptionThrown = false;

        try {
            kelly.put("dude",cacheEntry);
        } catch (KellyException e){
            kellyExceptionThrown = true;
        }

        assert kellyExceptionThrown;

    }

    @Test
    public void noCacheEntryTest() throws CacheProviderException, KellyException {

        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = Mockito.mock(CacheLoader.class);


        Mockito.when(cacheProvider.get("dude")).thenReturn(null);

        Kelly kelly = new Kelly(cacheProvider, cacheLoader, 1);

        assert kelly.get("dude") == null;

    }

    @Test
    public void reloadCacheEntryTest() throws CacheProviderException, KellyException, InterruptedException, CacheLoaderException {

        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = Mockito.mock(CacheLoader.class);

        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude", "suman karthik", 1);
        CacheEntry<String> cacheEntry1 = new CacheEntry<String>("dude", "suman", 1);

        Kelly<String> kelly = new Kelly<String>(cacheProvider, cacheLoader, 1);
        Mockito.when(cacheProvider.get("dude")).thenReturn(cacheEntry);
        Mockito.when(cacheLoader.reload("dude", "suman karthik")).thenReturn(cacheEntry1);

        assert kelly.get("dude").equals("suman karthik");

        Thread.sleep(3000);

        assert kelly.get("dude").equals("suman karthik");

        Thread.sleep(1000);

        Mockito.verify(cacheLoader).reload("dude","suman karthik");

        Mockito.verify(cacheProvider).put("dude", cacheEntry1);
    }

    @Test
    public void ttlZeroTest() throws CacheLoaderException, CacheProviderException, InterruptedException, KellyException {
        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = Mockito.mock(CacheLoader.class);

        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude", "suman karthik");

        Kelly<String> kelly = new Kelly<String>(cacheProvider, cacheLoader, 1);
        Mockito.when(cacheProvider.get("dude")).thenReturn(cacheEntry);

        assert kelly.get("dude").equals("suman karthik");

        Thread.sleep(1000);

        assert kelly.get("dude").equals("suman karthik");


        Mockito.verify(cacheLoader, Mockito.never()).reload("dude","suman karthik");
    }

    @Test
    public void kellyGetTest() throws CacheProviderException, KellyException {
        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = Mockito.mock(CacheLoader.class);
        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude","suman",1);


        Mockito.when(cacheProvider.get("dude")).thenReturn(cacheEntry);

        Kelly<String> kelly = new Kelly<String>(cacheProvider,cacheLoader,1);

        assert kelly.get("dude").equals("suman");

    }

    @Test
    public void kellyPutTest() throws CacheProviderException, KellyException {
        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = Mockito.mock(CacheLoader.class);
        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude","suman",1);


        Mockito.when(cacheProvider.put("dude",cacheEntry)).thenReturn(true);

        Kelly<String> kelly = new Kelly<String>(cacheProvider,cacheLoader,1);

        assert kelly.put("dude",cacheEntry);

        Mockito.verify(cacheProvider).put("dude", cacheEntry);

    }

    @Test(singleThreaded = true)
    public void requestsInFlightTest() throws InterruptedException, CacheProviderException, KellyException {


        CacheProvider<String> cacheProvider = Mockito.mock(CacheProvider.class);
        CacheLoader<String> cacheLoader = new DummyCacheLoader();

        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude","suman",1);

        Kelly<String> kelly = new Kelly<String>(cacheProvider,cacheLoader,10);

        Thread.sleep(2000);

        Mockito.when(cacheProvider.get("dude")).thenReturn(cacheEntry);

        kelly.get("dude");

        kelly.get("dude");

        Thread.sleep(1000);

        assert DummyCacheLoader.invocations==1;
    }

    @Test(dependsOnMethods = {"requestsInFlightTest"})
    public void expireTest() throws KellyException, CacheProviderException, InterruptedException {

        CacheEntry<String> cacheEntry = new CacheEntry<String>("dude","suman",300);

        CacheProvider<String> cacheProvider = new DummyCacheProvider();
        CacheLoader<String> cacheLoader = new DummyCacheLoader();

        Kelly<String> kelly = new Kelly<String>(cacheProvider,cacheLoader,10);

        kelly.put("dude",cacheEntry);

        kelly.expire("dude");
        assert cacheProvider.get("dude").getTtl()==-10;

        Thread.sleep(4000);

        assert kelly.get("dude").equals("suman karthik");
    }

}
