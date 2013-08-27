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
