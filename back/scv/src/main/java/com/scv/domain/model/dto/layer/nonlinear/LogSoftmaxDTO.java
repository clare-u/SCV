package com.scv.domain.model.dto.layer.nonlinear;


import com.scv.domain.model.dto.layer.LayerDTO;

public record LogSoftmaxDTO(
        String type,
        int dim
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}