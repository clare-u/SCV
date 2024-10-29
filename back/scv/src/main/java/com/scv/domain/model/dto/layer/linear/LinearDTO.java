package com.scv.domain.model.dto.layer.linear;

import com.scv.domain.model.dto.layer.LayerDTO;

public record LinearDTO(
        String type,
        int inFeatures,
        int outFeatures
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}