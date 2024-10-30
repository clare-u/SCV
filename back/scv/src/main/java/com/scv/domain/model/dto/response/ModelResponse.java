package com.scv.domain.model.dto.response;

import com.scv.domain.model.domain.Model;

import java.time.LocalDateTime;

public record ModelResponse(
        Long modelId,
        String modelName,
        String dataName,
        int latestNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ModelResponse(Model model) {
        this(
                model.getId(),
                model.getName(),
                model.getData().getName(),
                model.getLatestVersion(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
