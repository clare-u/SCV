package com.scv.domain.version.dto.layer.linear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("Linear")
@Getter
public class LinearDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "Linear";

    private final int inFeatures;
    private final int outFeatures;

    @JsonCreator
    public LinearDTO(
            @JsonProperty("in_features") int inFeatures,
            @JsonProperty("out_features") int outFeatures) {
        this.inFeatures = inFeatures;
        this.outFeatures = outFeatures;
    }
}
