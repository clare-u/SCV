package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record TanhDTO(
        String name
) implements LayerDTO {
    public TanhDTO() {
        this("Tanh");
    }
}