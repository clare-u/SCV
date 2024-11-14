package com.scv.global.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    private ResponseUtil() {
    }

    public static void sendResponse(HttpServletResponse response, int statusCode, String code, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = "{"
                + "\"httpStatus\": " + statusCode + ","
                + "\"code\": \"" + code + "\","
                + "\"message\": \"" + message + "\""
                + "}";

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
