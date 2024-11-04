package com.scv.domain.user.dto;

import com.scv.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCacheDTO {

    private Long userId;
    private String userUuid;
    private String userEmail;
    private String userNickname;
    private LocalDateTime userCreatedAt;
    private LocalDateTime userUpdatedAt;
    private boolean userIsDeleted;

    public static UserCacheDTO from(User user) {
        return UserCacheDTO.builder()
                .userId(user.getUserId())
                .userUuid(user.getUserUuid())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userCreatedAt(user.getUserCreatedAt())
                .userUpdatedAt(user.getUserUpdatedAt())
                .userIsDeleted(user.isUserIsDeleted())
                .build();
    }
}
