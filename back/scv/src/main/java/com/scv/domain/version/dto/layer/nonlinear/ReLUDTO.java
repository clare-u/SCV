package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ReLU")
@Getter
public class ReLUDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ReLU";

    @JsonCreator
    public ReLUDTO() {
    }
}
