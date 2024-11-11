package com.scv.global.jwt.service;

import com.scv.global.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {

    public RedisTokenService(
            @Qualifier("accessTokenBlacklistRedisTemplate") RedisTemplate<String, Object> accessTokenBlacklistRedisTemplate,
            @Qualifier("refreshTokenWhitelistRedisTemplate") RedisTemplate<String, Object> refreshTokenWhitelistRedisTemplate) {
        this.accessTokenBlacklistRedisTemplate = accessTokenBlacklistRedisTemplate;
        this.refreshTokenWhitelistRedisTemplate = refreshTokenWhitelistRedisTemplate;
    }

    private final RedisTemplate<String, Object> accessTokenBlacklistRedisTemplate;
    private final RedisTemplate<String, Object> refreshTokenWhitelistRedisTemplate;

    public void addToBlacklist(String accessToken) {
        Claims claims = JwtUtil.parseAccessTokenClaims(accessToken);

        long duration = claims.getExpiration().getTime() - System.currentTimeMillis();

        accessTokenBlacklistRedisTemplate.opsForValue().set(accessToken, true, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String accessToken) {
        Boolean isBlacklisted = (Boolean) accessTokenBlacklistRedisTemplate.opsForValue().get(accessToken);
        return Boolean.TRUE.equals(isBlacklisted);
    }

    public void addToWhitelist(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();
        long duration = claims.getExpiration().getTime() - System.currentTimeMillis();

        refreshTokenWhitelistRedisTemplate.opsForValue().set(userId, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isWhitelisted(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();

        return refreshToken.equals(refreshTokenWhitelistRedisTemplate.opsForValue().get(userId));
    }

    public void deleteFromWhitelist(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();

        refreshTokenWhitelistRedisTemplate.delete(userId);
    }
}
