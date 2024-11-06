package com.scv.domain.version.dto.layer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@JsonTypeName("Flatten")
@Getter
public class FlattenDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "Flatten";

    @JsonCreator
    public FlattenDTO() {
    }
}
