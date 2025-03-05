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

    @Override
    public void storeToken(String token, String userId, long expirationTime) {
    // 将 token 作为键，userId 作为值存储，并设置过期时间
        redisTemplate.opsForValue().set(token,userId,expirationTime, TimeUnit.SECONDS);
    }

    @Override
    public String getUserIdFromToken(String token) {
        //从Redis中获取用户ID
        return redisTemplate.opsForValue().get(token);
    }

    @Override
    public boolean isTokenValid(String token) {
        //检查Token是否存在
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    @Override
    public void deleteToken(String token) {
        //删除Token
        redisTemplate.delete(token);
    }

    @Override
    // 延长 Token 的有效期
    public boolean tokenExpiration(String token,long additionalTime){
        // 获取当前过期时间
        Long currentTtl = redisTemplate.getExpire(token,TimeUnit.SECONDS);
        if(currentTtl != null &&currentTtl < 1800){
        redisTemplate.expire(token,additionalTime,TimeUnit.SECONDS);
        }
        return true;
    }
}
