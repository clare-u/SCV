package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ELUDTO(
        String name,
        double alpha
) implements LayerDTO {
    public ELUDTO(double alpha) {
        this("ELU", alpha);
    }
}
