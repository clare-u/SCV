package com.scv.global.filter;

import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.oauth2.dto.OAuth2UserDTO;
import com.scv.global.util.CookieUtil;
import com.scv.global.util.JwtUtil;
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

import static com.scv.global.util.JwtUtil.*;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie accessTokenCookie = CookieUtil.getCookie(request, ACCESS_TOKEN_NAME);
        Cookie refreshTokenCookie = CookieUtil.getCookie(request, REFRESH_TOKEN_NAME);

        if (accessTokenCookie == null && request.getRequestURI().equals("/api/v1/users")) {
            return;
        }

        // 엑세스 토큰이 없으면 로그인
        if (accessTokenCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰이 위조됐으면 로그인
        if (JwtUtil.isAccessTokenTampered(accessTokenCookie.getValue())) {
            CookieUtil.deleteCookie(response, accessTokenCookie);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 리프레시 토큰이 없으면 로그인
        if (refreshTokenCookie == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 리프레시 토큰이 위조됐으면 로그인
        if (JwtUtil.isRefreshTokenTampered(refreshTokenCookie.getValue())) {
            CookieUtil.deleteCookie(response, refreshTokenCookie);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 엑세스 토큰이 만료됐을 때, 리프레시 토큰이 만료되지 않았으면 엑세스 토큰 유효시간 연장
        if (JwtUtil.isAccessTokenExpired(accessTokenCookie.getValue())) {
            String newAccessToken = JwtUtil.reIssueAccessToken(accessTokenCookie.getValue());
            accessTokenCookie = CookieUtil.createCookie(ACCESS_TOKEN_NAME, newAccessToken, ACCESS_TOKEN_EXPIRATION);
            response.addCookie(accessTokenCookie);
        }

        Claims accessTokenClaims = JwtUtil.parseAccessTokenClaims(accessTokenCookie.getValue());

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
