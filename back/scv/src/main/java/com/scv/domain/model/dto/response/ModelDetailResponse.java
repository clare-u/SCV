package com.scv.domain.model.dto.response;

import com.scv.domain.version.dto.response.ModelVersionDetail;
import com.scv.domain.version.dto.response.ModelVersionResponse;

import java.util.List;
import java.util.Optional;

public record ModelDetailResponse(
        List<ModelVersionResponse> modelVersions,
        Optional<ModelVersionDetail> detail
) {
    public ModelDetailResponse(List<ModelVersionResponse> modelVersions, ModelVersionDetail detail) {
        this(modelVersions, Optional.ofNullable(detail));
    }
}
