package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record GELUDTO(
        String name
) implements LayerDTO {
    @Override
    public String getName() {
        return name;
    }
}