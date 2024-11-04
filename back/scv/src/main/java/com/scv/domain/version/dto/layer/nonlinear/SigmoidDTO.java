package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record SigmoidDTO(
        String name
) implements LayerDTO {
    public SigmoidDTO() {
        this("Sigmoid");
    }
}