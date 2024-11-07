package com.scv.domain.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scv.global.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.scv.global.error.ErrorCode.INVALID_TOKEN;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String url = request.getServletPath();

        if (url.startsWith("/swagger-ui") || url.startsWith("/v3/api-docs")) {
            response.sendRedirect("/oauth2/authorization/github");
            return;
        }

        ErrorResponse errorResponse = new ErrorResponse(INVALID_TOKEN);
        response.setStatus(INVALID_TOKEN.getHttpStatus());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
