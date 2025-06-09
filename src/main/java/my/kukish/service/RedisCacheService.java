package my.kukish.service;

import redis.clients.jedis.Jedis;

public class RedisCacheService {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final int TTL_SECONDS = 15 * 60; // 15 минут

    public void save(String city, String dataJson) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            jedis.setex("weather:" + city.toLowerCase(), TTL_SECONDS, dataJson);
        }
    }

    public String get(String city) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            return jedis.get("weather:" + city.toLowerCase());
        }
    }
}
