package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ELU")
@Getter
public class ELUDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ELU";

    private final double alpha;

    @JsonCreator
    public ELUDTO(@JsonProperty("alpha") double alpha) {
        this.alpha = alpha;
    }
}
