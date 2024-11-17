package com.scv.global.jwt.filter;

import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
import com.scv.global.jwt.util.JwtUtil;
import com.scv.global.oauth2.service.RedisOAuth2AuthorizedClientService;
import com.scv.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.scv.global.jwt.util.JwtUtil.ACCESS_TOKEN_NAME;
import static com.scv.global.jwt.util.JwtUtil.REFRESH_TOKEN_NAME;

@Component
@RequiredArgsConstructor
public class CustomJwtLogoutFilter extends OncePerRequestFilter {

    private final RedisTokenService redisTokenService;
    private final RedisOAuth2AuthorizedClientService redisOAuth2AuthorizedClientService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/v1/logout") && request.getMethod().equalsIgnoreCase("POST")) {

            CookieUtil.getCookie(request, ACCESS_TOKEN_NAME)
                    .ifPresent(cookie -> redisTokenService.addToBlacklist(cookie.getValue()));

            CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
                    .ifPresent(cookie -> redisTokenService.deleteFromWhitelist(cookie.getValue()));

            CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
                    .ifPresent(cookie -> redisOAuth2AuthorizedClientService.removeAuthorizedClient("github", JwtUtil.parseRefreshTokenClaims(cookie.getValue()).getSubject()));

            CookieUtil.deleteCookie(response, ACCESS_TOKEN_NAME);
            CookieUtil.deleteCookie(response, REFRESH_TOKEN_NAME);

            ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, "CustomJwtLogoutFilter", "로그아웃이 성공했습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
