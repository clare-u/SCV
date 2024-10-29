package com.scv.domain.model.dto.layer.nonlinear;

import com.scv.domain.model.dto.layer.LayerDTO;

public record ReLUDTO(
        String type
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}