package com.scv.domain.version.dto.layer.linear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record LinearDTO(
        String name,
        int inFeatures,
        int outFeatures
) implements LayerDTO {
    public LinearDTO(int inFeatures, int outFeatures) {
        this("Linear", inFeatures, outFeatures);
    }
}