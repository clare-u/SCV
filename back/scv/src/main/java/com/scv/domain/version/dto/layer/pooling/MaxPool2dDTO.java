package com.scv.domain.version.dto.layer.pooling;

import com.scv.domain.version.dto.layer.LayerDTO;

public record MaxPool2dDTO(
        String name,
        int kernelSize,
        int stride
) implements LayerDTO {
    public MaxPool2dDTO(int kernelSize, int stride) {
        this("MaxPool2d", kernelSize, stride);
    }
}