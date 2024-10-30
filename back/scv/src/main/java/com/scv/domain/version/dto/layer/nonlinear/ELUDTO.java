package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ELUDTO(
        String name,
        double alpha
) implements LayerDTO {
    @Override
    public String getName() {
        return name;
    }
}