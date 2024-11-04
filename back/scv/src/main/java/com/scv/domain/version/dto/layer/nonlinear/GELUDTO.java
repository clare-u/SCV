package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("GELU")
@Getter
public class GELUDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "GELU";

    @JsonCreator
    public GELUDTO() {
    }
}
