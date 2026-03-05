package com.quickerrand.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成密钥
     * 确保密钥长度至少 256 位（32 字节）以满足 JWT HMAC-SHA 算法要求
     */
    private SecretKey getSecretKey() {
        try {
            byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
            
            // 如果密钥长度不足 32 字节（256 位），使用 SHA-256 哈希扩展
            if (secretBytes.length < 32) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                secretBytes = digest.digest(secretBytes);
            } else if (secretBytes.length > 32) {
                // 如果密钥长度超过 32 字节，截取前 32 字节
                byte[] truncated = new byte[32];
                System.arraycopy(secretBytes, 0, truncated, 0, 32);
                secretBytes = truncated;
            }
            
            return Keys.hmacShaKeyFor(secretBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法生成 JWT 密钥", e);
        }
    }

    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * 创建Token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

}
