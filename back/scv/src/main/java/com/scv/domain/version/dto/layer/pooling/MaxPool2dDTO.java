package com.scv.domain.version.dto.layer.pooling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("MaxPool2d")
@Getter
public class MaxPool2dDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "MaxPool2d";

    private final int kernelSize;
    private final int stride;

    @JsonCreator
    public MaxPool2dDTO(
            @JsonProperty("kernelSize") int kernelSize,
            @JsonProperty("stride") int stride) {
        this.kernelSize = kernelSize;
        this.stride = stride;
    }
}
