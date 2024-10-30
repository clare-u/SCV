from pydantic import BaseModel, Field
from typing import List, Literal, Union
import json

# Convolution Layers
class Conv2d(BaseModel):
    name: Literal["Conv2d"]
    in_channels: int
    out_channels: int
    kernel_size: int

class ConvTranspose2d(BaseModel):
    name: Literal["ConvTranspose2d"]
    in_channels: int
    out_channels: int
    kernel_size: int

# Pooling Layers
class MaxPool2d(BaseModel):
    name: Literal["MaxPool2d"]
    kernel_size: int
    stride: int

class AvgPool2d(BaseModel):
    name: Literal["AvgPool2d"]
    kernel_size: int
    stride: int

# Padding Layers
class ReflectionPad2d(BaseModel):
    name: Literal["ReflectionPad2d"]
    padding: int

class ReplicationPad2d(BaseModel):
    name: Literal["ReplicationPad2d"]
    padding: int

class ZeroPad2d(BaseModel):
    name: Literal["ZeroPad2d"]
    padding: int

class ConstantPad2d(BaseModel):
    name: Literal["ConstantPad2d"]
    padding: int
    value: float

# Non-Linear Activation Layers
class ReLU(BaseModel):
    name: Literal["ReLU"]

class LeakyReLU(BaseModel):
    name: Literal["LeakyReLU"]
    negative_slope: float

class ELU(BaseModel):
    name: Literal["ELU"]
    alpha: float

class PReLU(BaseModel):
    name: Literal["PReLU"]
    num_parameters: int
    init: float

class Sigmoid(BaseModel):
    name: Literal["Sigmoid"]

class Tanh(BaseModel):
    name: Literal["Tanh"]

class Softmax(BaseModel):
    name: Literal["Softmax"]
    dim: int

class LogSoftmax(BaseModel):
    name: Literal["LogSoftmax"]
    dim: int

class GELU(BaseModel):
    name: Literal["GELU"]

# Linear Layers
class Linear(BaseModel):
    name: Literal["Linear"]
    in_features: int
    out_features: int

# Union of All Layer names
Layer = Union[Conv2d, ConvTranspose2d, MaxPool2d, AvgPool2d, ReflectionPad2d, ReplicationPad2d, ZeroPad2d, 
              ConstantPad2d, ReLU, LeakyReLU, ELU, PReLU, Sigmoid, Tanh, Softmax, LogSoftmax, GELU, Linear]

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
    "Linear": Linear
}

def deserialize_layers(layers_json: str) -> List[Layer]:
    """JSON 문자열을 Layer 리스트로 역직렬화."""
    layer_dicts = json.loads(layers_json)
    
    layers = []
    for layer_dict in layer_dicts:
        layer_dict = json.loads(layer_dict)
        layer_type = layer_dict.get("name")
        layer_class = layer_classes.get(layer_type)
        if layer_class:
            layers.append(layer_class.parse_obj(layer_dict))
        else:
            raise ValueError(f"Unknown layer type: {layer_type}")
    return layers