package com.scv.domain.model.dto.layer.padding;

import com.scv.domain.model.dto.layer.LayerDTO;

public record ZeroPad2dDTO(
        String type,
        int padding
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}