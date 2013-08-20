import lego.gracekelly.api.CacheLoader;
import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheLoaderException;
import lego.gracekelly.exceptions.CacheProviderException;
import lego.gracekelly.helpers.LoaderCallable;
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
        LoaderCallable loaderCallable = new LoaderCallable(cacheProvider,cacheLoader, cacheEntry);
        Future cacheLoaded = executorService.submit(loaderCallable);

        Thread.sleep(500);

        Mockito.verify(cacheProvider).put("dude", cacheEntry1);
        assert cacheLoaded.get().equals(true);
    }

}
