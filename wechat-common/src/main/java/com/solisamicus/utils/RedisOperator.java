package com.solisamicus.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisOperator {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean keyIsExist(String key) {
        return redisTemplate.hasKey(key);
    }

    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public long incrementHash(String name, String key, long delta) {
        return redisTemplate.opsForHash().increment(name, key, delta);
    }

    public long decrementHash(String name, String key, long delta) {
        delta = delta * (-1);
        return redisTemplate.opsForHash().increment(name, key, delta);
    }

    public void setHashValue(String name, String key, String value) {
        redisTemplate.opsForHash().put(name, key, value);
    }

    public String getHashValue(String name, String key) {
        return (String) redisTemplate.opsForHash().get(name, key);
    }

    public long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * Deletes a specific key from Redis.
     *
     * @param key the key to delete
     */
    public void deleteKeyByKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Deletes all keys that match a given pattern from Redis.
     *
     * @param key the pattern to match keys (e.g., "user:" will match "user:1", "user:2", etc.)
     */
    public void deleteKeysByKey(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        redisTemplate.delete(keys);
    }


    /**
     * Deletes all key-value pairs where the value matches the specified target value.
     *
     * @param targetValue the value to match against the stored values
     */
    public void deleteKeysByValue(String targetValue) {
        Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(redisConnection -> redisConnection.scan(ScanOptions.scanOptions().match("*").count(1000).build()));
        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            String value = redisTemplate.opsForValue().get(key);
            if (targetValue.equals(value)) {
                redisTemplate.delete(key);
            }
        }
    }

    /**
     * Attempts to set the value of a key only if the key does not already exist.
     *
     * @param key   the key to set
     * @param value the value to set
     * @return true if the key was set, false if the key already exists
     */
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * Attempts to set the value of a key only if the key does not already exist, with a specified time to live (TTL).
     *
     * @param key     the key to set
     * @param value   the value to set
     * @param seconds the time to live for the key, in seconds
     * @return true if the key was set, false if the key already exists
     */
    public Boolean setIfAbsentWithTTL(String key, String value, Integer seconds) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * Sets the value of a key, updating the value if the key already exists.
     *
     * @param key   the key to set
     * @param value the value to set
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Sets the value of a key with a specified time to live (TTL), updating the value and TTL if the key already exists.
     *
     * @param key     the key to set
     * @param value   the value to set
     * @param seconds the time to live for the key, in seconds
     */
    public void set(String key, String value, Integer seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public List<String> mget(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public List<Object> batchGet(List<String> keys) {
        List<Object> result = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection src = (StringRedisConnection) connection;

                for (String k : keys) {
                    src.get(k);
                }
                return null;
            }
        });
        return result;
    }

    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public Map<Object, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public long lpush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public String lpop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    public long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public Long execLuaScript(String script, String key, String value) {
        return redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), value);
    }
}
