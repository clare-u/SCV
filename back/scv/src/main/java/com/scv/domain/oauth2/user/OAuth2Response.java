package com.scv.domain.oauth2.user;

import java.time.ZonedDateTime;

public interface OAuth2Response {

    String getUserEmail();

    String getUserImageUrl();

    String getUserNickname();

    ZonedDateTime getUserCreatedAt();

    ZonedDateTime getUserUpdatedAt();
}
