package com.scv.domain.version.dto.layer.padding;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ConstantPad2dDTO(
        String name,
        int padding,
        double value
) implements LayerDTO {
    public ConstantPad2dDTO(int padding, double value) {
        this("ConstantPad2d", padding, value);
    }
}
