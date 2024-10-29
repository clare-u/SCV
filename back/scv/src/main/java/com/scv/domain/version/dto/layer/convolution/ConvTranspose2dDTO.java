package com.scv.domain.version.dto.layer.convolution;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ConvTranspose2dDTO(
        String type,
        int inChannels,
        int outChannels,
        int kernelSize
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}