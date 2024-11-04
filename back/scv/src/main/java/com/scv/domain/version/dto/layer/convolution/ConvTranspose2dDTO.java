package com.scv.domain.version.dto.layer.convolution;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ConvTranspose2dDTO(
        String name,
        int inChannels,
        int outChannels,
        int kernelSize
) implements LayerDTO {
    public ConvTranspose2dDTO(int inChannels, int outChannels, int kernelSize) {
        this("ConvTranspose2d", inChannels, outChannels, kernelSize);
    }
}