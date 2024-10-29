package com.scv.domain.model.dto.layer.padding;

import com.scv.domain.model.dto.layer.LayerDTO;

public record ConstantPad2dDTO(
        String type,
        int padding,
        double value
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}