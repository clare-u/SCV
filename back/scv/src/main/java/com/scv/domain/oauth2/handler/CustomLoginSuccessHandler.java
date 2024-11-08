package com.scv.domain.oauth2.handler;

import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.global.util.CookieUtil;
import com.scv.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.scv.global.util.JwtUtil.*;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String accessToken = JwtUtil.createAccessToken((CustomOAuth2User) authentication.getPrincipal());
        String refreshToken = JwtUtil.createRefreshToken((CustomOAuth2User) authentication.getPrincipal());

        response.addCookie(CookieUtil.createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_EXPIRATION));
        response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_EXPIRATION));

//        response.sendRedirect("https://k11a107.p.ssafy.io");
        response.sendRedirect("http://localhost:3000/");
    }
}
