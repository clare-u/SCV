package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("LeakyReLU")
@Getter
public class LeakyReLUDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "LeakyReLU";

    private final double negativeSlope;

    @JsonCreator
    public LeakyReLUDTO(@JsonProperty("negative_slope") double negativeSlope) {
        this.negativeSlope = negativeSlope;
    }
}
