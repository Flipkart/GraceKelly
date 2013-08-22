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
