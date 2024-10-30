package com.scv.domain.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Builder
@Getter
public class OAuth2UserDTO {

    private Long userId;
    private String userUuid;
    private String userEmail;
    private String userImageUrl;
    private String userNickname;
    private ZonedDateTime userCreatedAt;
    private ZonedDateTime userUpdatedAt;
    private boolean userIsDeleted;
}
