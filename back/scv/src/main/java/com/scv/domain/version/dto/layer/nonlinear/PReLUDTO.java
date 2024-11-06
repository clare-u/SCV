package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("PReLU")
@Getter
public class PReLUDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "PReLU";

    private final int numParameters;
    private final double init;

    @JsonCreator
    public PReLUDTO(
            @JsonProperty("num_parameters") int numParameters,
            @JsonProperty("init") double init) {
        this.numParameters = numParameters;
        this.init = init;
    }
}
