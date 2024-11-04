package com.scv.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userEmail")
    private String userEmail;

    @JsonProperty("userImageUrl")
    private String userImageUrl;

    @JsonProperty("userNickname")
    private String userNickname;

    @JsonProperty("userRepo")
    private String userRepo;
}
