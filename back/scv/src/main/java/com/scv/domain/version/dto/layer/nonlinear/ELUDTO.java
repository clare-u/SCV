package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ELUDTO(
        String type,
        double alpha
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}