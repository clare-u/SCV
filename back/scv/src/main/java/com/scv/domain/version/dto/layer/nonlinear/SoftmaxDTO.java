package com.scv.domain.version.dto.layer.nonlinear;

import com.scv.domain.version.dto.layer.LayerDTO;

public record SoftmaxDTO(
        String name,
        int dim
) implements LayerDTO {
    public SoftmaxDTO(int dim) {
        this("Softmax", dim);
    }
}