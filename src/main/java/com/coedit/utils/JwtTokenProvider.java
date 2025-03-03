package com.coedit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")  // 从配置文件中注入密钥
    private String secret;

    @Value("${jwt.expiration}")  // 从配置文件中注入过期时间
    private long expiration;
    //生成安全密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    //生成token
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
       return buildToken(claims,username);
    }
    private String buildToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)          // 自定义声明（如角色）
                .setSubject(subject)        // 主题（通常放用户名）
                .setIssuedAt(new Date())    // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 签名算法
                .compact();
    }

    //解析验证token
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseToken(String token) {
     return Jwts.parser()
             .setSigningKey(getSigningKey())
             .build()
             .parseClaimsJws(token)
             .getBody();
    }
}
