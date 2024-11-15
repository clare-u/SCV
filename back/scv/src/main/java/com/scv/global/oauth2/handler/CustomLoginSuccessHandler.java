package com.scv.global.oauth2.handler;

import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
import com.scv.global.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.scv.global.jwt.util.JwtUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedisTokenService redisTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

//        CookieUtil.getCookie(request, ACCESS_TOKEN_NAME)
//                .ifPresent(cookie -> redisTokenService.addToBlacklist(cookie.getValue()));

//        CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
//                .ifPresent(cookie -> redisTokenService.deleteFromWhitelist(cookie.getValue()));

        log.error("CustomLoginSuccessHandler Start");
        CustomOAuth2User authUser = (CustomOAuth2User) authentication.getPrincipal();

        log.error("authUser.getUserId(): {}", authUser.getUserId());
        log.error("authUser.getUserUuid(): {}", authUser.getUserUuid());
        log.error("authUser.getUserNickname(): {}", authUser.getUserNickname());
        log.error("authUser.getUserRepo(): {}", authUser.getUserRepo());
        String accessToken = JwtUtil.createAccessToken(authUser);
        String refreshToken = JwtUtil.createRefreshToken(authUser);

        log.error("accessToken: {}", accessToken);
        log.error("refreshToken: {}", refreshToken);
        response.addCookie(CookieUtil.createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_EXPIRATION * 3));
        response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_EXPIRATION));
        log.error("accessToken and refreshToken added in response cookie");
        redisTokenService.addToWhitelist(refreshToken);

        log.error("refreshToken added in Redis");
        response.sendRedirect(System.getenv("DOMAIN"));
    }

}
