package com.coedit.service.intf;

import org.springframework.stereotype.Service;

@Service
public interface RedisTokenService {
    public void storeToken(String token,String userId,long expirationTime) ;

    public String getUserIdFromToken(String token);

    boolean isTokenValid(String token);

    public void deleteToken(String token);

    boolean tokenExpiration(String token,long additionalTime);
}
