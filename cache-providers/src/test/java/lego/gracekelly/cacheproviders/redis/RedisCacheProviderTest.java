package lego.gracekelly.cacheproviders.redis;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import lego.gracekelly.entities.CacheEntry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisCacheProviderTest {

  private RedisCacheProvider cacheProvider;
  private JedisSentinelPool pool;
  private Jedis jedis;
  private CacheEntry<String> cacheEntry = new CacheEntry<>();
  private String cacheEntryAsString =
      "{\"value\":null,\"key\":null,\"ttl\":0,\"epoch_timestamp\":0}";
  ObjectMapper mapper = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    setupJedis();
    cacheProvider = new RedisCacheProvider(pool, mapper);
  }

  private void setupJedis() {
    jedis = mock(Jedis.class);
    pool = mock(JedisSentinelPool.class);
    when(pool.getResource()).thenReturn(jedis);
  }

  @Test
  public void shouldDeserialize() throws Exception {
    String key = "TEST-KEY";
    when(jedis.get(key)).thenReturn(cacheEntryAsString);
    assertEquals(cacheEntryAsString, mapper.writeValueAsString(cacheProvider.get(key)));
  }

  @Test
  public void shouldSerialize() throws Exception {
    String key = "TEST-KEY";
    cacheProvider.put(key, cacheEntry);
    verify(jedis).set(key, cacheEntryAsString);
  }
}