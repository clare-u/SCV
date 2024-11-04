package com.scv.domain.version.dto.layer.nonlinear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.scv.domain.version.dto.layer.LayerDTO;
import lombok.Getter;

@JsonTypeName("LogSoftmax")
@Getter
public class LogSoftmaxDTO extends LayerDTO {

    @JsonProperty("name")
    private final String name = "LogSoftmax";

    private final int dim;

    @JsonCreator
    public LogSoftmaxDTO(@JsonProperty("dim") int dim) {
        this.dim = dim;
    }
}
