package com.scv.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final String ACCESS_TOKEN_NAME;
    private final String REFRESH_TOKEN_NAME;
    private final byte[] ACCESS_TOKEN_SECRET_KEY_BYTES;
    private final byte[] REFRESH_TOKEN_SECRET_KEY_BYTES;
    private final int ACCESS_TOKEN_EXPIRATION;
    private final int REFRESH_TOKEN_EXPIRATION;

    public JwtUtil(@Value("${spring.jwt.token.access.name}") String ACCESS_TOKEN_NAME,
                   @Value("${spring.jwt.token.refresh.name}") String REFRESH_TOKEN_NAME,
                   @Value("${spring.jwt.token.access.secret-key}") String ACCESS_TOKEN_SECRET_KEY,
                   @Value("${spring.jwt.token.refresh.secret-key}") String REFRESH_TOKEN_SECRET_KEY,
                   @Value("${spring.jwt.token.access.expiration}") int ACCESS_TOKEN_EXPIRATION,
                   @Value("${spring.jwt.token.refresh.expiration}") int REFRESH_TOKEN_EXPIRATION) {
        this.ACCESS_TOKEN_NAME = ACCESS_TOKEN_NAME;
        this.REFRESH_TOKEN_NAME = REFRESH_TOKEN_NAME;
        this.ACCESS_TOKEN_SECRET_KEY_BYTES = ACCESS_TOKEN_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        this.REFRESH_TOKEN_SECRET_KEY_BYTES = REFRESH_TOKEN_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        this.ACCESS_TOKEN_EXPIRATION = ACCESS_TOKEN_EXPIRATION;
        this.REFRESH_TOKEN_EXPIRATION = REFRESH_TOKEN_EXPIRATION;
    }

    private String createToken(String userUuid, String type, byte[] secretKeyBytes, long expiration) {
        return Jwts.builder()
                .setSubject(userUuid)
                .claim("type", type)
                .setIssuedAt(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(expiration).toInstant()))
                .signWith(Keys.hmacShaKeyFor(secretKeyBytes), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessToken(String userUuid) {
        return createToken(userUuid, ACCESS_TOKEN_NAME, ACCESS_TOKEN_SECRET_KEY_BYTES, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(String userUuid) {
        return createToken(userUuid, REFRESH_TOKEN_NAME, REFRESH_TOKEN_SECRET_KEY_BYTES, REFRESH_TOKEN_EXPIRATION);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                        @Override
                        public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                            String type = claims.get("type", String.class);
                            return type.equals(ACCESS_TOKEN_NAME) ? ACCESS_TOKEN_SECRET_KEY_BYTES : REFRESH_TOKEN_SECRET_KEY_BYTES;
                        }
                    })
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid token signature or malformed token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument provided for token: {}", e.getMessage());
        }

        return false;
    }

    public String getUserUuid(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(ACCESS_TOKEN_SECRET_KEY_BYTES).build()
                .parseClaimsJws(accessToken).getBody().getSubject();
    }
}
