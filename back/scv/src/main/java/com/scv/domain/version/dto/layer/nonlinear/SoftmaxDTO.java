package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record SoftmaxDTO(
        String type,
        int dim
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}