package com.scv.global.jwt.filter;

import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.global.oauth2.dto.OAuth2UserDTO;
import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
import com.scv.global.jwt.util.JwtUtil;
import com.scv.global.jwt.enums.TokenStatus;
import com.scv.global.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static com.scv.global.jwt.util.JwtUtil.*;
import static com.scv.global.jwt.enums.TokenStatus.EXPIRED;
import static com.scv.global.jwt.enums.TokenStatus.TAMPERED;

@Component
@RequiredArgsConstructor
public class JwtVerifyFilter extends OncePerRequestFilter {

    private final RedisTokenService redisTokenService;
    private final Set<String> whitelist = Set.of();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> accessTokenCookie = CookieUtil.getCookie(request, ACCESS_TOKEN_NAME);
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookie(request, REFRESH_TOKEN_NAME);

        // 유저 프로필 조회 API 는 특별 처리
        if (accessTokenCookie.isEmpty() && request.getRequestURI().equals("/api/v1/users")) {
            return;
        }

        // 화이트 리스트의 API 는 JwtFilter 스킵
        if (whitelist.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 나머지 API 는 엑세스 토큰이 없으면 필터 스킵
        if (accessTokenCookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰 및 상태 변수
        String accessToken = accessTokenCookie.get().getValue();
        TokenStatus accessTokenStatus = getAccessTokenStatus(accessToken);

        // 엑세스 토큰이 블랙리스트에 있거나 위조됐으면 예외 발생
        if (redisTokenService.isBlacklisted(accessToken) ||
                accessTokenStatus == TAMPERED) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
            return;
        }

        // 엑세스 토큰이 만료됐을 경우 로직
        if (accessTokenStatus == EXPIRED) {

            // 리프레시 토큰이 없으면 예외 발생
            if (refreshTokenCookie.isEmpty()) {
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "EXPIRED_TOKEN", "만료된 토큰입니다.");
                return;
            }

            // 리프레시 토큰 및 상태 변수
            String refreshToken = refreshTokenCookie.get().getValue();
            TokenStatus refreshTokenStatus = getRefreshTokenStatus(refreshToken);

            // 리프레시 토큰이 화이트리스트에 없거나 위조됐으면 예외 발생
            if (!redisTokenService.isWhitelisted(refreshToken) ||
                    refreshTokenStatus == TAMPERED) {
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
                return;
            }

            // 엑세스 토큰 재발급 로직
            accessToken = JwtUtil.reIssueAccessToken(accessToken);
            refreshToken = JwtUtil.reIssueRefreshToken(refreshToken);

            Cookie newAccessTokenCookie = CookieUtil.createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_EXPIRATION * 3);
            Cookie newRefreshTokenCookie = CookieUtil.createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_EXPIRATION);

            response.addCookie(newAccessTokenCookie);
            response.addCookie(newRefreshTokenCookie);
            redisTokenService.addToWhitelist(refreshToken);
        }

        Claims accessTokenClaims = JwtUtil.parseAccessTokenClaims(accessToken);

        OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.builder()
                .userId(Long.valueOf(accessTokenClaims.getSubject()))
                .userUuid(accessTokenClaims.get("userUuid", String.class))
                .userNickname(accessTokenClaims.get("userNickname", String.class))
                .userRepo(accessTokenClaims.get("userRepo", String.class))
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2UserDTO);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
