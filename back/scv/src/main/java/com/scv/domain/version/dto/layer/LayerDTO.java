package com.scv.domain.version.dto.layer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scv.domain.version.dto.layer.convolution.Conv2dDTO;
import com.scv.domain.version.dto.layer.convolution.ConvTranspose2dDTO;
import com.scv.domain.version.dto.layer.linear.LinearDTO;
import com.scv.domain.version.dto.layer.nonlinear.*;
import com.scv.domain.version.dto.layer.padding.ConstantPad2dDTO;
import com.scv.domain.version.dto.layer.padding.ReflectionPad2dDTO;
import com.scv.domain.version.dto.layer.padding.ReplicationPad2dDTO;
import com.scv.domain.version.dto.layer.padding.ZeroPad2dDTO;
import com.scv.domain.version.dto.layer.pooling.AvgPool2dDTO;
import com.scv.domain.version.dto.layer.pooling.MaxPool2dDTO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // "name" 필드로 구분
        include = JsonTypeInfo.As.PROPERTY,
        property = "name"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Conv2dDTO.class, name = "Conv2d"),
        @JsonSubTypes.Type(value = ConvTranspose2dDTO.class, name = "ConvTranspose2d"),
        @JsonSubTypes.Type(value = MaxPool2dDTO.class, name = "MaxPool2d"),
        @JsonSubTypes.Type(value = AvgPool2dDTO.class, name = "AvgPool2d"),
        @JsonSubTypes.Type(value = ReflectionPad2dDTO.class, name = "ReflectionPad2d"),
        @JsonSubTypes.Type(value = ReplicationPad2dDTO.class, name = "ReplicationPad2d"),
        @JsonSubTypes.Type(value = ZeroPad2dDTO.class, name = "ZeroPad2d"),
        @JsonSubTypes.Type(value = ConstantPad2dDTO.class, name = "ConstantPad2d"),
        @JsonSubTypes.Type(value = ReLUDTO.class, name = "ReLU"),
        @JsonSubTypes.Type(value = LeakyReLUDTO.class, name = "LeakyReLU"),
        @JsonSubTypes.Type(value = ELUDTO.class, name = "ELU"),
        @JsonSubTypes.Type(value = PReLUDTO.class, name = "PReLU"),
        @JsonSubTypes.Type(value = SoftmaxDTO.class, name = "Softmax"),
        @JsonSubTypes.Type(value = LogSoftmaxDTO.class, name = "LogSoftmax"),
        @JsonSubTypes.Type(value = GELUDTO.class, name = "GELU"),
        @JsonSubTypes.Type(value = LinearDTO.class, name = "Linear")
})
public interface LayerDTO {
}