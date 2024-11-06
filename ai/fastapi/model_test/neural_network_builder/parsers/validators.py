"""
Pydantic을 사용한 데이터 검증 스키마 정의
각 레이어 타입별 필수 파라미터와 검증 규칙 정의
예: Conv2d는 in_channels, out_channels, kernel_size 등이 필수
ModelConfig, ModelLayerConfig 등 전체 모델 구성 스키마 정의
"""
from pydantic import BaseModel, Field, field_validator
from typing import List, Literal, Union, Optional, Dict, Any
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
            raise ValueError(f'{info.field_name} 는 양수여야 합니다.')
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


class ModelLayerConfig(BaseModel):
    layers: List[Layer]

    def model_dump(self) -> Dict[str, Any]:
        return {
            "layers": [layer.model_dump() for layer in self.layers]
        }

class ModelConfig(BaseModel):
    modelLayerAt: ModelLayerConfig
    dataName: str
    dataTrainCnt: int = Field(gt=0)
    dataTestCnt: int = Field(gt=0)
    dataLabelCnt: int = Field(gt=0)
    dataEpochCnt: int = Field(gt=0)
    versionNo: Optional[int] = None

    def model_dump(self) -> Dict[str, Any]:
        return {
            "modelLayerAt": self.modelLayerAt.model_dump(),
            "dataName": self.dataName,
            "dataTrainCnt": self.dataTrainCnt,
            "dataTestCnt": self.dataTestCnt,
            "dataLabelCnt": self.dataLabelCnt,
            "dataEpochCnt": self.dataEpochCnt,
            "versionNo": self.versionNo
        }

    @field_validator('dataName')
    def validate_data_name(cls, v):
        valid_datasets = ['MNIST', 'FASHION_MNIST', 'CIFAR10', 'SVHN', 'EMNIST']
        if v not in valid_datasets:
            raise ValueError(f'데이터셋은 {valid_datasets}에 존재하는 것 중 선택해야합니다.')
        return v


def deserialize_layers(layers_json: str) -> List[Layer]:
    """JSON 문자열을 Layer 리스트로 역직렬화합니다."""
    try:
        if isinstance(layers_json, str):
            config = json.loads(layers_json)
        else:
            config = layers_json

        # ModelConfig 검증
        valid_config = ModelConfig(**config)

        # 검증된 레이어 리스트 반환
        return valid_config.modelLayerAt.layers

    except json.JSONDecodeError as e:
        raise ValueError(f"잘못된 JSON 형식: {str(e)}")
    except KeyError as e:
        raise ValueError(f"필수 키 누락: {str(e)}")
    except Exception as e:
        raise ValueError(f"역직렬화 중 오류: {str(e)}")
