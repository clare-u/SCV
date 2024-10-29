package com.scv.domain.oauth2.user;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
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

    public LocalDateTime getUserCreatedAt() {
        return (LocalDateTime) attributes.get("created_at");
    }

    public LocalDateTime getUserUpdatedAt() {
        return (LocalDateTime) attributes.get("updated_at");
    }
}
