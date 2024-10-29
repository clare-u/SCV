package com.scv.domain.model.dto.layer.nonlinear;

import com.scv.domain.model.dto.layer.LayerDTO;

public record SigmoidDTO(
        String type
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}