package com.scv.domain.version.dto.layer.convolution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("ConvTranspose2d")
@Getter
public class ConvTranspose2dDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "ConvTranspose2d";

    private final int inChannels;
    private final int outChannels;
    private final int kernelSize;

    @JsonCreator
    public ConvTranspose2dDTO(
            @JsonProperty("inChannels") int inChannels,
            @JsonProperty("outChannels") int outChannels,
            @JsonProperty("kernelSize") int kernelSize) {
        this.inChannels = inChannels;
        this.outChannels = outChannels;
        this.kernelSize = kernelSize;
    }
}
