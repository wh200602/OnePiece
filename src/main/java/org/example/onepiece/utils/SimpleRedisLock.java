package org.example.onepiece.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock {

    private String name;
    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID().toString() + "-";
    @Override
    public boolean tryLock(long timeoutSec) {
        String id =ID_PREFIX + Thread.currentThread().getId();
        Boolean b = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + name, id, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(b);
    }

    @Override
    public void unlock() {
        String id =ID_PREFIX + Thread.currentThread().getId();
        String s = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
        if(id.equals(s)) {
            stringRedisTemplate.delete(KEY_PREFIX + name);
        }
    }
}
