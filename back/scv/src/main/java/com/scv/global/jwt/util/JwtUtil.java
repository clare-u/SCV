package com.scv.global.jwt.util;

import com.scv.domain.user.domain.User;
import com.scv.global.jwt.enums.TokenStatus;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class JwtUtil {

    public static final String ACCESS_TOKEN_NAME = loadEnv("JWT_ACCESS_NAME");
    public static final String REFRESH_TOKEN_NAME = loadEnv("JWT_REFRESH_NAME");
    public static final byte[] ACCESS_TOKEN_SECRET_KEY_BYTES = loadEnv("JWT_ACCESS_KEY").getBytes(StandardCharsets.UTF_8);
    public static final byte[] REFRESH_TOKEN_SECRET_KEY_BYTES = loadEnv("JWT_REFRESH_KEY").getBytes(StandardCharsets.UTF_8);
    public static final int ACCESS_TOKEN_EXPIRATION = Integer.parseInt(loadEnv("JWT_ACCESS_EXPIRATION"));
    public static final int REFRESH_TOKEN_EXPIRATION = Integer.parseInt(loadEnv("JWT_REFRESH_EXPIRATION"));

    private JwtUtil() {
    }

    private static String loadEnv(String name) {
        return System.getenv(name);
    }

    public static String createAccessToken(CustomOAuth2User authUser) {
        return Jwts.builder()
                .setSubject(authUser.getUserId().toString())
                .claim("userUuid", authUser.getUserUuid())
                .claim("userNickname", authUser.getUserNickname())
                .claim("userRepo", authUser.getUserRepo())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(getExpirationDate(ACCESS_TOKEN_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("userUuid", user.getUserUuid())
                .claim("userNickname", user.getUserNickname())
                .claim("userRepo", user.getUserRepo())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(getExpirationDate(ACCESS_TOKEN_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createRefreshToken(CustomOAuth2User authUser) {
        return Jwts.builder()
                .setSubject(authUser.getUserId().toString())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(getExpirationDate(REFRESH_TOKEN_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Date getExpirationDate(int expiration) {
        return Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(expiration).toInstant());
    }

    public static TokenStatus getAccessTokenStatus(String accessToken) {
        try {
            getTokenStatus(accessToken, ACCESS_TOKEN_SECRET_KEY_BYTES);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.TAMPERED;
        }
    }

    public static TokenStatus getRefreshTokenStatus(String refreshToken) {
        try {
            getTokenStatus(refreshToken, REFRESH_TOKEN_SECRET_KEY_BYTES);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.TAMPERED;
        }
    }

    private static void getTokenStatus(String token, byte[] secretKeyBytes) {
        Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String reIssueAccessToken(String accessToken) {
        Claims claims = parseAccessTokenClaims(accessToken);
        return Jwts.builder()
                .setSubject(claims.getSubject())
                .claim("userUuid", claims.get("userUuid"))
                .claim("userNickname", claims.get("userNickname"))
                .claim("userRepo", claims.get("userRepo"))
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(getExpirationDate(ACCESS_TOKEN_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String reIssueRefreshToken(String refreshToken) {
        Claims claims = parseRefreshTokenClaims(refreshToken);
        return Jwts.builder()
                .setSubject(claims.getSubject())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(getExpirationDate(REFRESH_TOKEN_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseAccessTokenClaims(String accessToken) {
        return parseTokenClaims(accessToken, ACCESS_TOKEN_SECRET_KEY_BYTES);
    }

    public static Claims parseRefreshTokenClaims(String refreshToken) {
        return parseTokenClaims(refreshToken, REFRESH_TOKEN_SECRET_KEY_BYTES);
    }

    private static Claims parseTokenClaims(String token, byte[] secretKeyBytes) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
