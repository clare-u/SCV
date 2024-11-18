package com.scv.global.util;

import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
import com.scv.global.jwt.util.JwtUtil;
import com.scv.global.oauth2.service.RedisOAuth2AuthorizedClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.scv.global.jwt.util.JwtUtil.ACCESS_TOKEN_NAME;
import static com.scv.global.jwt.util.JwtUtil.REFRESH_TOKEN_NAME;

@Component
@RequiredArgsConstructor
public class CustomResponse {

    private final RedisTokenService redisTokenService;
    private final RedisOAuth2AuthorizedClientService redisOAuth2AuthorizedClientService;

    public void sendResponse(HttpServletRequest request, HttpServletResponse response, int statusCode, String code, String message) throws IOException {
        CookieUtil.getCookie(request, ACCESS_TOKEN_NAME)
                .ifPresent(cookie -> redisTokenService.addToBlacklist(cookie.getValue()));

        CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
                .ifPresent(cookie -> redisTokenService.deleteFromWhitelist(cookie.getValue()));

        CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
                .ifPresent(cookie -> redisOAuth2AuthorizedClientService.removeAuthorizedClient("github", JwtUtil.parseRefreshTokenClaims(cookie.getValue()).getSubject()));

        CookieUtil.deleteCookie(response, ACCESS_TOKEN_NAME);
        CookieUtil.deleteCookie(response, REFRESH_TOKEN_NAME);

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = "{"
                + "\"httpStatus\": " + statusCode + ","
                + "\"code\": \"" + code + "\","
                + "\"message\": \"" + message + "\""
                + "}";

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
