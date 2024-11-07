package com.scv.domain.model.dto.response;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.model.domain.Model;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;

import java.time.LocalDateTime;

public record ModelResponse(
        UserProfileResponseDTO userProfile,
        Long modelId,
        String modelName,
        DataSet dataName,
        int latestVersion,
        double accuracy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ModelResponse(Model model) {
        this(
                new UserProfileResponseDTO(model.getUser()),
                model.getId(),
                model.getName(),
                model.getData().getName(),
                model.getLatestVersion() != null ? model.getLatestVersion() : 0,
                model.getAccuracy() != null ? model.getAccuracy() : -1.0, // null일 -값 보여주기
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
