package com.scv.global.oauth2.handler;

import com.scv.global.jwt.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String url = request.getServletPath();

        if (url.startsWith("/swagger-ui") || url.startsWith("/v3/api-docs")) {
            response.sendRedirect("/api/oauth2/authorization/github");
            return;
        }

        throw InvalidTokenException.getInstance();
    }
}
