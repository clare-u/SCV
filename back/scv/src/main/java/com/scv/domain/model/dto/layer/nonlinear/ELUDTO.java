package com.scv.domain.model.dto.layer.nonlinear;

import com.scv.domain.model.dto.layer.LayerDTO;

public record ELUDTO(
        String type,
        float alpha
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}