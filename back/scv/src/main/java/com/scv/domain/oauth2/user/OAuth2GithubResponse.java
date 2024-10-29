package com.scv.domain.oauth2.user;

import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2GithubResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final String userEmail;

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public String getUserImageUrl() {
        return attributes.get("avatar_url").toString();
    }

    @Override
    public String getUserNickname() {
        return attributes.get("login").toString();
    }

    @Override
    public ZonedDateTime getUserCreatedAt() {
        return ZonedDateTime.parse(String.valueOf(attributes.get("created_at")), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public ZonedDateTime getUserUpdatedAt() {
        return ZonedDateTime.parse(String.valueOf(attributes.get("updated_at")), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
