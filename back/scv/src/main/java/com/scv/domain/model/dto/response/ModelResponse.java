package com.scv.domain.model.dto.response;

import com.scv.domain.model.domain.Model;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;

import java.time.LocalDateTime;

public record ModelResponse(
        Long userId,
        UserProfileResponseDTO userProfileResponseDTO,
        Long modelId,
        String modelName,
        String dataName,
        int latestNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ModelResponse(User user, Model model) {
        this(
                user.getUserId(),
                new UserProfileResponseDTO(user),
                model.getId(),
                model.getName(),
                model.getData().getName(),
                model.getLatestVersion(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
