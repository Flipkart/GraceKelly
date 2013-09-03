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

import lego.gracekelly.Kelly;
import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheLoaderException;
import lego.gracekelly.exceptions.CacheProviderException;
import lego.gracekelly.LoaderCallable;
import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoaderCallableTest {

    private CacheLoader<String> cacheLoader;
    private CacheProvider<String> cacheProvider;
    private CacheEntry<String> cacheEntry;
    CacheEntry<String> cacheEntry1;

    @BeforeTest
    public void setUp() throws CacheLoaderException, CacheProviderException {
        cacheLoader = Mockito.mock(CacheLoader.class);
        cacheProvider = Mockito.mock(CacheProvider.class);

        cacheEntry = new CacheEntry<String>("dude", "suman", 300);

        cacheEntry1 = new CacheEntry<String>("dude", "suman karthik", 300);


        Mockito.when(cacheLoader.reload("dude","suman")).thenReturn(cacheEntry1);
        Mockito.when(cacheProvider.put("dude",cacheEntry1)).thenReturn(true);
    }

    @Test
    public void testCallable() throws CacheProviderException, InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Kelly<String> kelly = Mockito.mock(Kelly.class);
        LoaderCallable loaderCallable = new LoaderCallable(kelly,cacheProvider,cacheLoader, cacheEntry);
        Future cacheLoaded = executorService.submit(loaderCallable);

        Thread.sleep(500);

        Mockito.verify(cacheProvider).put("dude", cacheEntry1);
        assert cacheLoaded.get().equals(true);
    }

}
