package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("Sigmoid")
@Getter
public class SigmoidDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "Sigmoid";

    @JsonCreator
    public SigmoidDTO() {
    }
}
