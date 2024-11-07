package com.scv.domain.version.dto.response;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.version.domain.ModelVersion;

import java.time.LocalDateTime;

public record ModelVersionOnWorking(
        UserProfileResponseDTO userProfile,
        String title,
        Long modelVersionId,
        int version,
        DataSet dataName,
        double accuracy,
        LocalDateTime createdAt
) {
    public ModelVersionOnWorking(ModelVersion modelVersion) {
        this(
                new UserProfileResponseDTO(modelVersion.getModel().getUser()),
                modelVersion.getModel().getName(),
                modelVersion.getId(),
                modelVersion.getVersionNo(),
                modelVersion.getModel().getData().getName(),
                modelVersion.getResult() != null ? modelVersion.getResult().getTestAccuracy() : 0.0,
                modelVersion.getCreatedAt()
        );
    }
}
