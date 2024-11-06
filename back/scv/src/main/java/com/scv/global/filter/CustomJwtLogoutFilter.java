package com.scv.global.filter;

import com.scv.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.scv.global.util.JwtUtil.ACCESS_TOKEN_NAME;
import static com.scv.global.util.JwtUtil.REFRESH_TOKEN_NAME;

@Component
public class CustomJwtLogoutFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/api/v1/logout") && request.getMethod().equalsIgnoreCase("POST")) {
            CookieUtil.deleteCookie(response, ACCESS_TOKEN_NAME);
            CookieUtil.deleteCookie(response, REFRESH_TOKEN_NAME);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
