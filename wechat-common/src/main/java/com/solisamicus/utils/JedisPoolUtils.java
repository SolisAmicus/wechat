package com.solisamicus.utils;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;

import static com.solisamicus.constants.JedisConstants.*;

public class JedisPoolUtils {

    @Getter
    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(MAX_TOTAL);
        poolConfig.setMaxIdle(MAX_IDLE);
        poolConfig.setMinIdle(MIN_IDLE);
        poolConfig.setMaxWaitMillis(MAX_WAIT_MILLIS);
        jedisPool = new JedisPool(poolConfig, HOST, PORT, TIMEOUT, PASSWORD);
    }

    public static Integer selectPort() {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> ports = jedis.hgetAll(NETTY_PORT);
        List<Integer> portList = ports
                .keySet()
                .stream()
                .map(Integer::valueOf)
                .toList();
        Integer port;
        if (portList.isEmpty()) {
            jedis.hset(NETTY_PORT, String.valueOf(DEFAULT_PORT), ONLINE_COUNTS);
            port = DEFAULT_PORT;
        } else {
            Integer currentPort = portList
                    .stream()
                    .max(Integer::compareTo)
                    .get()
                    .intValue() + STEP;
            jedis.hset(NETTY_PORT, String.valueOf(currentPort), ONLINE_COUNTS);
            port = currentPort;
        }
        return port;
    }
}
