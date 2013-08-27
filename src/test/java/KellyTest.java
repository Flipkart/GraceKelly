import lego.gracekelly.Kelly;
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

    @Test
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

}
