package redis.clients.jedis.examples;

import org.junit.Assert;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisProtocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.csc.Cache;
import redis.clients.jedis.csc.CacheConfig;

import java.util.HashMap;
import java.util.Map;

public class RedisClientSideCacheUsage {
    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 6379;
        HostAndPort endpoint = new HostAndPort(host, port);

        DefaultJedisClientConfig config = DefaultJedisClientConfig
                .builder()
                .password("foobared")
                .protocol(RedisProtocol.RESP3)
                .build();

        CacheConfig cacheConfig = CacheConfig.builder().maxSize(1000).build();

        try (UnifiedJedis client = new UnifiedJedis(endpoint, config, cacheConfig)) {
            Cache cache = client.getCache();

            // value stored in Redis server
            client.set("key", "value");
            // cache is empty
            Assert.assertEquals(0, cache.getSize());
            // value retrieved from Redis server and stored in cache
            Assert.assertEquals("value", client.get("key"));
            // cache size is 1
            Assert.assertEquals(1, client.getCache().getSize());

            Map<String, String> person = new HashMap<>();
            person.put("name", "John Doe");
            person.put("age", "30");
            person.put("city", "New York");
            person.put("country", "USA");
            person.put("email", "johndoe99@gmail.com");

            // add the person map to Redis
            client.hset("person:1", person);
            Assert.assertEquals(1, cache.getSize());

            // person map retrieved from Redis server and stored in cache
            Assert.assertEquals(person, client.hgetAll("person:1"));
            Assert.assertEquals(2, client.getCache().getSize());

            // cache is cleared
            cache.flush();
            Assert.assertEquals(0, cache.getSize());

        }

    }
}
