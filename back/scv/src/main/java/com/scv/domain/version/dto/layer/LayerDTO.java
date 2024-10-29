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
        use = JsonTypeInfo.Id.NAME, // "type" 필드로 구분
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Conv2dDTO.class, name = "conv2d"),
        @JsonSubTypes.Type(value = ConvTranspose2dDTO.class, name = "convtranspose2d"),
        @JsonSubTypes.Type(value = MaxPool2dDTO.class, name = "maxpool2d"),
        @JsonSubTypes.Type(value = AvgPool2dDTO.class, name = "avgpool2d"),
        @JsonSubTypes.Type(value = ReflectionPad2dDTO.class, name = "reflectionpad2d"),
        @JsonSubTypes.Type(value = ReplicationPad2dDTO.class, name = "replicationpad2d"),
        @JsonSubTypes.Type(value = ZeroPad2dDTO.class, name = "zeropad2d"),
        @JsonSubTypes.Type(value = ConstantPad2dDTO.class, name = "constantpad2d"),
        @JsonSubTypes.Type(value = ReLUDTO.class, name = "relu"),
        @JsonSubTypes.Type(value = LeakyReLUDTO.class, name = "leakyrelu"),
        @JsonSubTypes.Type(value = ELUDTO.class, name = "elu"),
        @JsonSubTypes.Type(value = PReLUDTO.class, name = "prelu"),
        @JsonSubTypes.Type(value = SoftmaxDTO.class, name = "softmax"),
        @JsonSubTypes.Type(value = LogSoftmaxDTO.class, name = "logsoftmax"),
        @JsonSubTypes.Type(value = GELUDTO.class, name = "gelu"),
        @JsonSubTypes.Type(value = LinearDTO.class, name = "linear")
})
public interface LayerDTO {
    String getType();
}