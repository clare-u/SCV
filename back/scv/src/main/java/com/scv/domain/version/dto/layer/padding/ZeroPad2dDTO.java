package com.scv.domain.version.dto.layer.padding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ZeroPad2d")
@Getter
public class ZeroPad2dDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ZeroPad2d";

    private final int padding;

    @JsonCreator
    public ZeroPad2dDTO(@JsonProperty("padding") int padding) {
        this.padding = padding;
    }
}
