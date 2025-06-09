package my.kukish.test;

import redis.clients.jedis.Jedis;

public class RedisTest {
    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            System.out.println(jedis.ping());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}