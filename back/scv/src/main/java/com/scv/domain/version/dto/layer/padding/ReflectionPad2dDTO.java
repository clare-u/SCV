package com.scv.domain.version.dto.layer.padding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ReflectionPad2d")
@Getter
public class ReflectionPad2dDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ReflectionPad2d";

    private final int padding;

    @JsonCreator
    public ReflectionPad2dDTO(@JsonProperty("padding") int padding) {
        this.padding = padding;
    }
}
