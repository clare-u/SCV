package com.scv.domain.model.dto.response;

import com.scv.domain.version.domain.ModelVersion;

public record ModelCreateResponse(
        Long modelId,
        Long modelVersionId
) {
    public ModelCreateResponse(ModelVersion modelVersion) {
        this(
                modelVersion.getModel().getId(),
                modelVersion.getId()
        );
    }
}
