package com.scv.domain.model.dto.response;

import com.scv.domain.model.domain.Model;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.dto.response.ModelVersionDetail;
import com.scv.domain.version.dto.response.ModelVersionResponse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record ModelDetailResponse(
        UserProfileResponseDTO userInfo,
        Long modelId,
        String modelName,
        String DataName,
        int latestVersion,
        List<ModelVersionResponse> modelVersions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ModelDetailResponse(Model model) {
        this(
                new UserProfileResponseDTO(model.getUser()),
                model.getId(),
                model.getName(),
                model.getData().getName().toString(),
                model.getLatestVersion(),
                model.getModelVersions().stream()
                        .sorted(Comparator.comparing(ModelVersion::getVersionNo).reversed())
                        .map(ModelVersionResponse::new)
                        .collect(Collectors.toList()),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
