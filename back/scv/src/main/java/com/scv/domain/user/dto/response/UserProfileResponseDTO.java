package com.scv.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scv.domain.user.domain.User;
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

    public UserProfileResponseDTO(User user) {
        this.userId = user.getUserId();
        this.userEmail = user.getUserEmail();
        this.userImageUrl = user.getUserImageUrl();
        this.userNickname = user.getUserNickname();
    }

}
