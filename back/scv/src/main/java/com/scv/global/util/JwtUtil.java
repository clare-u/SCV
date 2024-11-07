package com.scv.global.util;

import com.scv.domain.oauth2.CustomOAuth2User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class JwtUtil {

    public static final String ACCESS_TOKEN_NAME = System.getenv("JWT_ACCESS_NAME");
    public static final String REFRESH_TOKEN_NAME = System.getenv("JWT_REFRESH_NAME");
    public static final byte[] ACCESS_TOKEN_SECRET_KEY_BYTES = System.getenv("JWT_ACCESS_KEY").getBytes(StandardCharsets.UTF_8);
    public static final byte[] REFRESH_TOKEN_SECRET_KEY_BYTES = System.getenv("JWT_REFRESH_KEY").getBytes(StandardCharsets.UTF_8);
    public static final int ACCESS_TOKEN_EXPIRATION = Integer.parseInt(System.getenv("JWT_ACCESS_EXPIRATION"));
    public static final int REFRESH_TOKEN_EXPIRATION = Integer.parseInt(System.getenv("JWT_REFRESH_EXPIRATION"));

    public static String createAccessToken(CustomOAuth2User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("userUuid", user.getUserUuid())
                .claim("userNickname", user.getUserNickname())
                .claim("userRepo", user.getUserRepo())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(ACCESS_TOKEN_EXPIRATION).toInstant()))
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createRefreshToken(CustomOAuth2User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(REFRESH_TOKEN_EXPIRATION).toInstant()))
                .signWith(Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY_BYTES), SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean isAccessTokenTampered(String accessToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(ACCESS_TOKEN_SECRET_KEY_BYTES)
                    .build()
                    .parseClaimsJws(accessToken);

            String algorithm = claimsJws.getHeader().getAlgorithm();
            if (SignatureAlgorithm.NONE.getValue().equalsIgnoreCase(algorithm)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRefreshTokenTampered(String refreshToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(REFRESH_TOKEN_SECRET_KEY_BYTES)
                    .build()
                    .parseClaimsJws(refreshToken);

            String algorithm = claimsJws.getHeader().getAlgorithm();
            if (SignatureAlgorithm.NONE.getValue().equalsIgnoreCase(algorithm)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAccessTokenExpired(String accessToken) {
        return false;
    }

    public static boolean isRefreshTokenExpired(String refreshToken) {
        return false;
    }

    public static String reIssueAccessToken(String accessToken) {
        return "";
    }

    public static String reIssueRefreshToken(String refreshToken) {
        return "";
    }

    public static Claims parseAccessTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY_BYTES))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
