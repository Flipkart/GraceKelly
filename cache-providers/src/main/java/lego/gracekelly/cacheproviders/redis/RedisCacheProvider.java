package lego.gracekelly.cacheproviders.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import lego.gracekelly.api.CacheProvider;
import lego.gracekelly.entities.CacheEntry;
import lego.gracekelly.exceptions.CacheProviderException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class RedisCacheProvider implements CacheProvider<String> {

  private final JedisSentinelPool pool;
  private final ObjectMapper mapper;

  public RedisCacheProvider(JedisSentinelPool pool, ObjectMapper mapper) {
    this.pool = pool;
    this.mapper = mapper;
  }

  @Override
  public CacheEntry<String> get(String key) throws CacheProviderException {
    //pool can only be null if sentinelPool wasn't established
    //in which case, alternate cache should've been used
    //and this shouldn't have been called
    assert pool != null;
    try (Jedis jedis = pool.getResource()) {
      try {
        String valueFromCache = jedis.get(key);
        if (StringUtils.isNotBlank(valueFromCache)) {
          return mapper.readValue(valueFromCache, new TypeReference<CacheEntry<String>>() {});
        } else {
          return null;
        }
      } catch (Exception e) {
        throw new CacheProviderException(e);
      }
    }
  }

  @Override
  public Boolean put(String key, CacheEntry<String> cacheEntry) throws CacheProviderException {
    //pool can only be null if sentinelPool wasn't established
    //in which case, alternate cache should've been used
    //and this shouldn't have been called
    assert pool != null;
    try (Jedis jedis = pool.getResource()) {
      jedis.set(key, mapper.writeValueAsString(cacheEntry));
      return true;
    } catch (Exception e) {
      throw new CacheProviderException(e);
    }
  }
}
