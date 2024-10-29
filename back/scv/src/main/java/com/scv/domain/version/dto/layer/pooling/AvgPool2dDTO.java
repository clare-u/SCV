package com.scv.domain.version.dto.layer.pooling;

import com.scv.domain.version.dto.layer.LayerDTO;

public record AvgPool2dDTO(
        String type,
        int kernelSize,
        int stride
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}