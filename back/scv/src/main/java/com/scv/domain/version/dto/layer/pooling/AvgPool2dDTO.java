package com.scv.domain.version.dto.layer.pooling;

import com.scv.domain.version.dto.layer.LayerDTO;

public record AvgPool2dDTO(
        String name,
        int kernelSize,
        int stride
) implements LayerDTO {
    public AvgPool2dDTO(int kernelSize, int stride) {
        this("AvgPool2d", kernelSize, stride);
    }
}
