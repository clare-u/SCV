package com.scv.domain.version.dto.layer.convolution;

import com.scv.domain.version.dto.layer.LayerDTO;

public record Conv2dDTO(
        String name,
        int inChannels,
        int outChannels,
        int kernelSize
) implements LayerDTO {
    public Conv2dDTO(int inChannels, int outChannels, int kernelSize) {
        this("Conv2d", inChannels, outChannels, kernelSize);
    }
}