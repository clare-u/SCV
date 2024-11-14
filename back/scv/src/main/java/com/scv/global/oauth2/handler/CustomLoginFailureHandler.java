package com.scv.global.oauth2.handler;

import com.scv.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ResponseUtil.sendResponse(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "CustomLoginFailureHandler", "소셜 로그인에 실패했습니다.");
    }
}
