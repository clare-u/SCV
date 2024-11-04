package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ReLUDTO(
        String name
) implements LayerDTO {
    public ReLUDTO() {
        this("ReLU");
    }
}