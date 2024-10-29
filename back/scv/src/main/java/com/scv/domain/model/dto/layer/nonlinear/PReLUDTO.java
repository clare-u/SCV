package com.scv.domain.model.dto.layer.nonlinear;

import com.scv.domain.model.dto.layer.LayerDTO;

public record PReLUDTO(
        String type,
        int numParameters,
        double init
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}