package com.scv.global.jwt.service;

import com.scv.global.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void addToBlacklist(String accessToken) {
        Claims claims = JwtUtil.parseAccessTokenClaims(accessToken);

        long duration = claims.getExpiration().getTime() - System.currentTimeMillis();
        String key = "blacklist:" + accessToken;

        redisTemplate.opsForValue().set(key, true, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String accessToken) {
        String key = "blacklist:" + accessToken;
        Boolean isBlacklisted = (Boolean) redisTemplate.opsForValue().get(key);
        return Boolean.TRUE.equals(isBlacklisted);
    }

    public void addToWhitelist(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();
        long duration = claims.getExpiration().getTime() - System.currentTimeMillis();
        String key = "whitelist:" + userId;

        redisTemplate.opsForValue().set(key, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isWhitelisted(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();
        String key = "whitelist:" + userId;

        return refreshToken.equals(redisTemplate.opsForValue().get(key));
    }

    public void deleteFromWhitelist(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();
        String key = "whitelist:" + userId;

        redisTemplate.delete(key);
    }
}
