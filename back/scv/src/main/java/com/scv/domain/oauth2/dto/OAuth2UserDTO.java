package com.scv.domain.oauth2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class OAuth2UserDTO {

    private Long userId;
    private String userUuid;
    private String userNickname;
    private String userRepo;
}
