package com.scv.global.jwt.filter;

import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/v1/logout") && request.getMethod().equalsIgnoreCase("POST")) {

            CookieUtil.getCookie(request, ACCESS_TOKEN_NAME)
                    .ifPresent(cookie -> redisTokenService.addToBlacklist(cookie.getValue()));

            CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
                    .ifPresent(cookie -> redisTokenService.deleteFromWhitelist(cookie.getValue()));

            CookieUtil.deleteCookie(response, ACCESS_TOKEN_NAME);
            CookieUtil.deleteCookie(response, REFRESH_TOKEN_NAME);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            String jsonResponse = "{"
                    + "\"httpStatus\": 200,"
                    + "\"code\": \"LOGOUT_SUCCESS\","
                    + "\"message\": \"Logout successful\""
                    + "}";

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            return;
        }

        filterChain.doFilter(request, response);
    }
}
