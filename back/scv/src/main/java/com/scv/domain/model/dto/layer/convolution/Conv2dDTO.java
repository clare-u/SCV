package com.scv.domain.model.dto.layer.convolution;

import com.scv.domain.model.dto.layer.LayerDTO;

public record Conv2dDTO(
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