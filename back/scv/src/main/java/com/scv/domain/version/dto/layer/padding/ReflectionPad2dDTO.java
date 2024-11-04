package com.scv.domain.version.dto.layer.padding;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ReflectionPad2dDTO(
        String name,
        int padding
) implements LayerDTO {
    public ReflectionPad2dDTO(int padding) {
        this("ReflectionPad2d", padding);
    }
}
