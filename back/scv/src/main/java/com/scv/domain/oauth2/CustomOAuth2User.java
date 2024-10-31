package com.scv.domain.oauth2;

import com.scv.domain.oauth2.dto.OAuth2UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserDTO oauth2UserDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return oauth2UserDTO.getUserUuid();
    }

    public Long getUserId() {
        return oauth2UserDTO.getUserId();
    }

    public String getUserUuid() {
        return oauth2UserDTO.getUserUuid();
    }

    public String getUserEmail() {
        return oauth2UserDTO.getUserEmail();
    }

    public String getUserImageUrl() {
        return oauth2UserDTO.getUserImageUrl();
    }

    public String getUserNickname() {
        return oauth2UserDTO.getUserNickname();
    }

    public ZonedDateTime getUserCreatedAt() {
        return oauth2UserDTO.getUserCreatedAt();
    }

    public ZonedDateTime getUserUpdatedAt() {
        return oauth2UserDTO.getUserUpdatedAt();
    }

    public boolean getUserIsDeleted() {
        return oauth2UserDTO.isUserIsDeleted();
    }
}
