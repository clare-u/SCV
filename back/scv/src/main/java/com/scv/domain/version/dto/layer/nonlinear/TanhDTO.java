package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("Tanh")
@Getter
public class TanhDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "Tanh";

    @JsonCreator
    public TanhDTO() {
    }
}
