package com.coedit.service.impl;

import com.coedit.service.intf.RedisTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenServiceImpl implements RedisTokenService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long MIN_REFRESH_THRESHOLD = 1800; // 30分钟
    private static final long DEFAULT_EXPIRATION = 3600; // 1小时

    @Override
    public void storeToken(String token, String userId, long expirationTime) {
        if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
            return;
        }
        
        if (expirationTime <= 0) {
            expirationTime = DEFAULT_EXPIRATION;
        }
        redisTemplate.opsForValue().set(token, userId, expirationTime, TimeUnit.SECONDS);
    }

    @Override
    public String getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return redisTemplate.opsForValue().get(token);
    }

    @Override
    public boolean isTokenValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String userId = getUserIdFromToken(token);
        return userId != null && !userId.isEmpty();
    }

    @Override
    public void deleteToken(String token) {
        if (token != null && !token.isEmpty()) {
            redisTemplate.delete(token);
        }
    }

    @Override
    public boolean tokenExpiration(String token, long additionalTime) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Long currentTtl = redisTemplate.getExpire(token, TimeUnit.SECONDS);
        if (currentTtl == null || currentTtl < 0) {
            return false;
        }

        // 如果剩余时间小于阈值，则延长过期时间
        if (currentTtl < MIN_REFRESH_THRESHOLD) {
            if (additionalTime <= 0) {
                additionalTime = DEFAULT_EXPIRATION;
            }
            return redisTemplate.expire(token, additionalTime, TimeUnit.SECONDS);
        }

        return true;
    }
}
