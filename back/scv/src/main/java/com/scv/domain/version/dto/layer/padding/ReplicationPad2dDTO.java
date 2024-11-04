package com.scv.domain.version.dto.layer.padding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ReplicationPad2d")
@Getter
public class ReplicationPad2dDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ReplicationPad2d";

    private final int padding;

    @JsonCreator
    public ReplicationPad2dDTO(@JsonProperty("padding") int padding) {
        this.padding = padding;
    }
}
