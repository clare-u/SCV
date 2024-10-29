package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record TanhDTO(
        String type
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}