package com.scv.domain.oauth2;

import com.scv.domain.oauth2.dto.OAuth2UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserDTO oauth2UserDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
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

    public String getUserNickname() {
        return oauth2UserDTO.getUserNickname();
    }

    public String getUserRepo() {
        return oauth2UserDTO.getUserRepo();
    }
}
