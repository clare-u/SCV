package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record SigmoidDTO(
        String type
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}