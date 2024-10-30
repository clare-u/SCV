package com.scv.domain.version.dto.layer.convolution;

import com.scv.domain.version.dto.layer.LayerDTO;

public record Conv2dDTO(
        String name,
        int inChannels,
        int outChannels,
        int kernelSize
) implements LayerDTO {
    @Override
    public String getName() {
        return name;
    }
}