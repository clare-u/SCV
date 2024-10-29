package com.scv.domain.model.dto.layer.nonlinear;

import com.scv.domain.model.dto.layer.LayerDTO;

public record LeakyReLUDTO(
        String type,
        float negativeSlope
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}