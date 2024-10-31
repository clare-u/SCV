package com.scv.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class CustomJwtLogoutFilter extends GenericFilterBean {

    @Value("${spring.jwt.token.access.name}")
    private String ACCESS_TOKEN_NAME;

    @Value("${spring.jwt.token.refresh.name}")
    private String REFRESH_TOKEN_NAME;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestURI = request.getRequestURI();
        if (!requestURI.matches("^/api/v1/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ACCESS_TOKEN_NAME)) {
                accessToken = cookie.getValue();
            } else if (cookie.getName().equals(REFRESH_TOKEN_NAME)) {
                refreshToken = cookie.getValue();
            }
        }

        response.addCookie(createCookie(ACCESS_TOKEN_NAME, accessToken));
        response.addCookie(createCookie(REFRESH_TOKEN_NAME, refreshToken));

//        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(0);
//        cookie.setSecure(true);
        cookie.setPath("/");
//        cookie.setHttpOnly(true);

        return cookie;
    }
}
