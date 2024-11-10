package com.scv.global.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public static void deleteCookie(HttpServletResponse response, String key) {
        Cookie cookie = new Cookie(key, null);
        deleteCookie(response, cookie);
    }

    public static void deleteCookie(HttpServletResponse response, Cookie cookie) {
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String key) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookie.getName().equals(key))
                .findFirst();
    }
}
