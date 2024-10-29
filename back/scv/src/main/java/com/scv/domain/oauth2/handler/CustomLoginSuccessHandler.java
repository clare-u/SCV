package com.scv.domain.oauth2.handler;

import com.scv.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.token.access.expiration}")
    private int ACCESS_TOKEN_EXPIRATION;

    @Value("${spring.jwt.token.refresh.expiration}")
    private int REFRESH_TOKEN_EXPIRATION;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String accessToken = jwtUtil.createAccessToken(authentication.getName());
        String refreshToken = jwtUtil.createRefreshToken(authentication.getName());

        response.addCookie(createCookie("access_token", accessToken, ACCESS_TOKEN_EXPIRATION));
        response.addCookie(createCookie("refresh_token", refreshToken, REFRESH_TOKEN_EXPIRATION));

        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value, int expiration) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiration);
//        cookie.setSecure(true);
        cookie.setPath("/");
//        cookie.setHttpOnly(true);

        return cookie;
    }
}
