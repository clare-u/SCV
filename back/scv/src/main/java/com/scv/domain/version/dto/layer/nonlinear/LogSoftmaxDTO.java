package com.scv.domain.version.dto.layer.nonlinear;


import com.scv.domain.version.dto.layer.LayerDTO;

public record LogSoftmaxDTO(
        String name,
        int dim
) implements LayerDTO {
    @Override
    public String getName() {
        return name;
    }
}