package com.scv.domain.version.dto.layer.padding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ConstantPad2d")
@Getter
public class ConstantPad2dDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ConstantPad2d";

    private final int padding;
    private final double value;

    @JsonCreator
    public ConstantPad2dDTO(
            @JsonProperty("padding") int padding,
            @JsonProperty("value") double value) {
        this.padding = padding;
        this.value = value;
    }
}
