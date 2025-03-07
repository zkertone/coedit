package com.coedit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌提供者
 * 负责JWT令牌的生成、解析和验证
 */
@Component
public class JwtTokenProvider {
    /**
     * JWT密钥
     * 从配置文件中注入，用于生成和验证JWT令牌
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT令牌过期时间（毫秒）
     * 从配置文件中注入
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * JWT解析器
     * 用于解析和验证JWT令牌，在构造函数中初始化
     */
    private final JwtParser jwtParser;
    
    /**
     * 构造函数
     * 初始化JWT解析器
     * @param secret JWT密钥
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }
    
    /**
     * 生成安全密钥
     * @return 用于签名JWT的密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT令牌
     * @param username 用户名
     * @return JWT令牌字符串
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, username);
    }
    
    /**
     * 构建JWT令牌
     * @param claims 自定义声明
     * @param subject 主题（通常是用户名）
     * @return JWT令牌字符串
     */
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)             // 自定义声明（如角色）
                .subject(subject)           // 主题（通常放用户名）
                .issuedAt(new Date())       // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 过期时间
                .signWith(getSigningKey())  // 签名算法（默认使用 HS256）
                .compact();
    }

    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @return 如果令牌有效返回true，否则返回false
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中获取指定的声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @return 解析后的声明值
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return JWT声明集
     */
    private Claims parseToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}
