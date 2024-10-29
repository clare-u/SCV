package com.scv.domain.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class OAuth2UserDTO {

    private Long userId;
    private UUID userUuid;
    private String userEmail;
    private String userImageUrl;
    private String userNickname;
    private LocalDateTime userCreatedAt;
    private LocalDateTime userUpdatedAt;
    private boolean userIsDeleted;
}
