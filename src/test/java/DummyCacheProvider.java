import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;

import java.util.HashMap;
import java.util.Map;

public class DummyCacheProvider implements CacheProvider<String>{

    private final Map<String,CacheEntry<String>> cache = new HashMap<String, CacheEntry<String>>();

    @Override
    public CacheEntry<String> get(String key) throws CacheProviderException {
        return cache.get(key);
    }

    @Override
    public Boolean put(String key, CacheEntry<String> value) throws CacheProviderException {
        cache.put(key,value);
        return true;
    }
}
