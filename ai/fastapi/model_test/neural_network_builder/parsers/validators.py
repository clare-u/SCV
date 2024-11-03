from pydantic import BaseModel, Field, field_validator
from typing import List, Literal, Union, Optional
import json

class Flatten(BaseModel):
    name: Literal["Flatten"]

# Convolution Layers
class Conv2d(BaseModel):
    name: Literal["Conv2d"]
    in_channels: int = Field(gt=0)
    out_channels: int = Field(gt=0)
    kernel_size: int = Field(gt=0)
    stride: Optional[int] = Field(default=1, gt=0)
    padding: Optional[int] = Field(default=0, ge=0)

    @field_validator('kernel_size', 'stride', 'padding')
    @classmethod
    def validate_positive(cls, v: int, info) -> int:
        if v <= 0:
            raise ValueError(f'{info.field_name} must be positive')
        return v


class ConvTranspose2d(BaseModel):
    name: Literal["ConvTranspose2d"]
    in_channels: int = Field(gt=0)
    out_channels: int = Field(gt=0)
    kernel_size: int = Field(gt=0)
    stride: Optional[int] = Field(default=1, gt=0)
    padding: Optional[int] = Field(default=0, ge=0)


# Pooling Layers
class MaxPool2d(BaseModel):
    name: Literal["MaxPool2d"]
    kernel_size: int = Field(gt=0)
    stride: Optional[int] = None
    padding: Optional[int] = Field(default=0, ge=0)


class AvgPool2d(BaseModel):
    name: Literal["AvgPool2d"]
    kernel_size: int = Field(gt=0)
    stride: Optional[int] = None
    padding: Optional[int] = Field(default=0, ge=0)


# Padding Layers
class ReflectionPad2d(BaseModel):
    name: Literal["ReflectionPad2d"]
    padding: int = Field(ge=0)


class ReplicationPad2d(BaseModel):
    name: Literal["ReplicationPad2d"]
    padding: int = Field(ge=0)


class ZeroPad2d(BaseModel):
    name: Literal["ZeroPad2d"]
    padding: int = Field(ge=0)


class ConstantPad2d(BaseModel):
    name: Literal["ConstantPad2d"]
    padding: int = Field(ge=0)
    value: float = Field(default=0.0)


# Non-Linear Activation Layers
class ReLU(BaseModel):
    name: Literal["ReLU"]
    inplace: Optional[bool] = Field(default=False)


class LeakyReLU(BaseModel):
    name: Literal["LeakyReLU"]
    negative_slope: float = Field(default=0.01)
    inplace: Optional[bool] = Field(default=False)


class ELU(BaseModel):
    name: Literal["ELU"]
    alpha: float = Field(default=1.0)
    inplace: Optional[bool] = Field(default=False)


class PReLU(BaseModel):
    name: Literal["PReLU"]
    num_parameters: int = Field(default=1, gt=0)
    init: float = Field(default=0.25)


class Sigmoid(BaseModel):
    name: Literal["Sigmoid"]


class Tanh(BaseModel):
    name: Literal["Tanh"]


class Softmax(BaseModel):
    name: Literal["Softmax"]
    dim: int = Field(default=1)


class LogSoftmax(BaseModel):
    name: Literal["LogSoftmax"]
    dim: int = Field(default=1)


class GELU(BaseModel):
    name: Literal["GELU"]


# Linear Layers
class Linear(BaseModel):
    name: Literal["Linear"]
    in_features: int = Field(gt=0)
    out_features: int = Field(gt=0)
    bias: Optional[bool] = Field(default=True)


# Union of All Layer types
Layer = Union[Conv2d, ConvTranspose2d, MaxPool2d, AvgPool2d, ReflectionPad2d, ReplicationPad2d, ZeroPad2d,
ConstantPad2d, ReLU, LeakyReLU, ELU, PReLU, Sigmoid, Tanh, Softmax, LogSoftmax, GELU, Linear, Flatten]

# Layer type mapping
layer_classes = {
    "Conv2d": Conv2d,
    "ConvTranspose2d": ConvTranspose2d,
    "MaxPool2d": MaxPool2d,
    "AvgPool2d": AvgPool2d,
    "ReflectionPad2d": ReflectionPad2d,
    "ReplicationPad2d": ReplicationPad2d,
    "ZeroPad2d": ZeroPad2d,
    "ConstantPad2d": ConstantPad2d,
    "ReLU": ReLU,
    "LeakyReLU": LeakyReLU,
    "ELU": ELU,
    "PReLU": PReLU,
    "Sigmoid": Sigmoid,
    "Tanh": Tanh,
    "Softmax": Softmax,
    "LogSoftmax": LogSoftmax,
    "GELU": GELU,
    "Linear": Linear,
    "Flatten": Flatten,
}


class ModelConfig(BaseModel):
    model: List[Layer]


def deserialize_layers(layers_json: str) -> List[Layer]:
    """JSON 문자열을 Layer 리스트로 역직렬화합니다."""
    try:
        if isinstance(layers_json, str):
            config = json.loads(layers_json)
        else:
            config = layers_json

        # ModelConfig를 사용하여 전체 구조 검증
        model_config = ModelConfig(model=config["model"])
        return model_config.model

    except json.JSONDecodeError as e:
        raise ValueError(f"Invalid JSON format: {str(e)}")
    except KeyError as e:
        raise ValueError(f"Missing required key: {str(e)}")
    except Exception as e:
        raise ValueError(f"Error during deserialization: {str(e)}")