package com.scv.global.oauth2.user;

import java.time.LocalDateTime;

public interface OAuth2Response {

    String getUserEmail();

    String getUserImageUrl();

    String getUserNickname();

    LocalDateTime getUserCreatedAt();

    LocalDateTime getUserUpdatedAt();
}
