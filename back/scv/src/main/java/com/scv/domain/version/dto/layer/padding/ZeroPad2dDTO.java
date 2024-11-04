package com.scv.domain.version.dto.layer.padding;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ZeroPad2dDTO(
        String name,
        int padding
) implements LayerDTO {
    public ZeroPad2dDTO(int padding) {
        this("ZeroPad2d", padding);
    }
}