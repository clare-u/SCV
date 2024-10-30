package com.scv.domain.version.dto.layer.linear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record LinearDTO(
        String name,
        int inFeatures,
        int outFeatures
) implements LayerDTO {
    @Override
    public String getName() {
        return name;
    }
}