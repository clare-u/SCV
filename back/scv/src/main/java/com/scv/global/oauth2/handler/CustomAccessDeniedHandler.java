package com.scv.global.oauth2.handler;

import com.scv.global.util.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final CustomResponse customResponse;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        customResponse.sendResponse(request, response, HttpServletResponse.SC_FORBIDDEN, "CustomAccessDeniedHandler", "인가되지 않은 사용자입니다.");
    }
}
