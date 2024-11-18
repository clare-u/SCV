package com.scv.global.jwt.filter;

import com.scv.global.util.CustomResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomJwtLogoutFilter extends OncePerRequestFilter {

    private final CustomResponse customResponse;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/api/v1/logout") && request.getMethod().equalsIgnoreCase("POST")) {
            customResponse.sendResponse(request, response, HttpServletResponse.SC_OK, "CustomJwtLogoutFilter", "로그아웃이 성공했습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
