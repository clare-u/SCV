package com.scv.global.jwt.service;

import com.scv.global.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RedisTokenService {

    public RedisTokenService(
            @Qualifier("accessMasterTemplate") RedisTemplate<String, Object> accessTokenBlacklistRedisMasterTemplate,
            @Qualifier("accessSlaveTemplates") List<RedisTemplate<String, Object>> accessTokenBlacklistRedisSlaveTemplates,
            @Qualifier("refreshMasterTemplate") RedisTemplate<String, Object> refreshTokenWhitelistRedisMasterTemplate) {
        this.accessTokenBlacklistRedisMasterTemplate = accessTokenBlacklistRedisMasterTemplate;
        this.accessTokenBlacklistRedisSlaveTemplates = accessTokenBlacklistRedisSlaveTemplates;
        this.refreshTokenWhitelistRedisMasterTemplate = refreshTokenWhitelistRedisMasterTemplate;
        this.slaveCnt = accessTokenBlacklistRedisSlaveTemplates.size();
    }

    private final RedisTemplate<String, Object> accessTokenBlacklistRedisMasterTemplate;
    private final List<RedisTemplate<String, Object>> accessTokenBlacklistRedisSlaveTemplates;
    private final RedisTemplate<String, Object> refreshTokenWhitelistRedisMasterTemplate;

    private final AtomicInteger slaveIndex = new AtomicInteger(0);
    private final int slaveCnt;

    public void addToBlacklist(String accessToken) {
        Claims claims = JwtUtil.parseAccessTokenClaims(accessToken);

        long duration = claims.getExpiration().getTime() - System.currentTimeMillis();

        accessTokenBlacklistRedisMasterTemplate.opsForValue().set(accessToken, true, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String accessToken) {
        System.out.println(slaveIndex);
        RedisTemplate<String, Object> accessTokenBlacklistRedisSlaveTemplate = getNextAccessTokenBlacklistRedisSlaveTemplate();
        Boolean isBlacklisted = (Boolean) accessTokenBlacklistRedisSlaveTemplate.opsForValue().get(accessToken);
        return Boolean.TRUE.equals(isBlacklisted);
    }

    private RedisTemplate<String, Object> getNextAccessTokenBlacklistRedisSlaveTemplate() {
        int index = slaveIndex.getAndIncrement() % slaveCnt;
        return accessTokenBlacklistRedisSlaveTemplates.get(index);
    }

    public void addToWhitelist(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();
        long duration = claims.getExpiration().getTime() - System.currentTimeMillis();

        refreshTokenWhitelistRedisMasterTemplate.opsForValue().set(userId, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    public boolean isWhitelisted(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();

        return refreshToken.equals(refreshTokenWhitelistRedisMasterTemplate.opsForValue().get(userId));
    }

    public void deleteFromWhitelist(String refreshToken) {
        Claims claims = JwtUtil.parseRefreshTokenClaims(refreshToken);

        String userId = claims.getSubject();

        refreshTokenWhitelistRedisMasterTemplate.delete(userId);
    }

    // 매일 자정에 초기화 (00:00)
    @Scheduled(cron = "0 0 0 * * *")
    public void resetSlaveIndex() {
        slaveIndex.set(0);
    }
}
