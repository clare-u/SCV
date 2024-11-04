package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record LeakyReLUDTO(
        String name,
        double negativeSlope
) implements LayerDTO {
    public LeakyReLUDTO(double negativeSlope) {
        this("LeakyReLU", negativeSlope);
    }
}