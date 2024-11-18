package com.scv.global.oauth2.handler;

import com.scv.global.util.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final CustomResponse customResponse;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String url = request.getServletPath();

        if (url.startsWith("/swagger-ui") || url.startsWith("/v3/api-docs")) {
            response.sendRedirect("/api/oauth2/authorization/github");
            return;
        }

        customResponse.sendResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, "CustomAuthenticationEntryPoint", "인증되지 않은 사용자입니다.");
    }
}
