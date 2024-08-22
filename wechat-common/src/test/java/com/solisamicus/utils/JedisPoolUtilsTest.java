package com.solisamicus.utils;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.solisamicus.constants.JedisConstants.NETTY_PORT;

public class JedisPoolUtilsTest {
    @Test
    public void test() {
        Integer port = JedisPoolUtils.selectPort();
        System.out.println(port);
    }
}
