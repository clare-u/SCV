package com.scv.domain.version.dto.response;

import com.scv.domain.version.domain.ModelVersion;

public record ModelVersionDetail(
        Long modelVersionId,
        String layers
) {
    public ModelVersionDetail(ModelVersion modelVersion) {
        this(
                modelVersion.getId(),
                modelVersion.getLayers()
        );
    }
}
