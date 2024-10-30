package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record PReLUDTO(
        String name,
        int numParameters,
        double init
) implements LayerDTO {
    @Override
    public String getName() {
        return name;
    }
}