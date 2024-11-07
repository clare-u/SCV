package com.scv.domain.model.dto.response;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.model.domain.Model;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;

import java.time.LocalDateTime;

public record ModelResponse(
        UserProfileResponseDTO userProfile,
        Long modelId,
        String modelName,
        DataSet dataName,
        int latestVersion,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ModelResponse(User user, Model model) {
        this(
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
