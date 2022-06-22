package com.syh.passbook.passbook.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushAll();
            return null;
        });

        Assert.assertNull(redisTemplate.opsForValue().get("name"));
        redisTemplate.opsForValue().set("name", "ben");
        Assert.assertNotNull(redisTemplate.opsForValue().get("name"));
        Assert.assertEquals("ben", redisTemplate.opsForValue().get("name"));
    }
}
