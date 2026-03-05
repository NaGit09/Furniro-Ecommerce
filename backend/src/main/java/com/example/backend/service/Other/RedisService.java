package com.example.backend.service.Other;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate caching;

    public void addData(String key, String value) {
        caching.opsForValue().set(key, value);
    }

    public void removeData(String key) {
        caching.delete(key);
    }

    public String getData(String key) {
        return caching.opsForValue().get(key);
    }
}
