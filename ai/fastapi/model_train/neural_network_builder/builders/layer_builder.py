"""
PyTorch의 각종 레이어(Conv2d, Linear 등)를 실제로 생성하는 빌더 클래스
각 레이어 타입별로 build 메소드를 제공
레이어 객체를 받아서 실제 PyTorch 모듈로 변환
"""
import torch.nn as nn
from typing import Dict, Any, Optional
from neural_network_builder.utils.logger import setup_logger
from neural_network_builder.exceptions.custom_exceptions import BuilderError
from neural_network_builder.parsers.validators import Layer, layer_classes

logger = setup_logger(__name__)


class LayerBuilder:
    """신경망 레이어 빌더"""

    def __init__(self):
        self._layer_builders = {
            # Convolution Layers
            "Conv2d": self._build_conv2d,
            "ConvTranspose2d": self._build_conv_transpose2d,

            # Pooling Layers
            "MaxPool2d": self._build_maxpool2d,
            "AvgPool2d": self._build_avgpool2d,

            # Padding Layers
            "ReflectionPad2d": self._build_reflection_pad2d,
            "ReplicationPad2d": self._build_replication_pad2d,
            "ZeroPad2d": self._build_zero_pad2d,
            "ConstantPad2d": self._build_constant_pad2d,

            # Non-Linear Activation Layers
            "ReLU": self._build_relu,
            "LeakyReLU": self._build_leaky_relu,
            "ELU": self._build_elu,
            "PReLU": self._build_prelu,
            "Sigmoid": self._build_sigmoid,
            "Tanh": self._build_tanh,
            "Softmax": self._build_softmax,
            "LogSoftmax": self._build_log_softmax,
            "GELU": self._build_gelu,

            # Linear Layers
            "Linear": self._build_linear,

            "Flatten": self._build_flatten,
        }

    def build(self, layer: Layer) -> nn.Module:
        """Layer 객체 -> PyTorch 레이어 변환"""
        try:
            if not hasattr(layer, 'name'):
                raise BuilderError(f"레이어에 name 속성이 없습니다: {layer}")

            builder = self._layer_builders.get(layer.name)
            if not builder:
                raise BuilderError(f"지원하지 않는 레이어 타입입니다.: {layer.name}")

            return builder(layer)

        except Exception as e:
            logger.error(f"레이어 생성 중 오류 발생 {layer.name if hasattr(layer, 'name') else 'Unknown'}: {str(e)}")
            raise BuilderError(f"레이어 생성 중 실패: {str(e)}")

    # Convolution Layers
    def _build_conv2d(self, layer: Layer) -> nn.Conv2d:
        return nn.Conv2d(
            in_channels=layer.in_channels,
            out_channels=layer.out_channels,
            kernel_size=layer.kernel_size,
            # stride=getattr(layer, 'stride', 1),
            # padding=getattr(layer, 'padding', 0)
        )

    def _build_conv_transpose2d(self, layer: Layer) -> nn.ConvTranspose2d:
        return nn.ConvTranspose2d(
            in_channels=layer.in_channels,
            out_channels=layer.out_channels,
            kernel_size=layer.kernel_size,
            # stride=getattr(layer, 'stride', 1),
            # padding=getattr(layer, 'padding', 0)
        )

    # Pooling Layers
    def _build_maxpool2d(self, layer: Layer) -> nn.MaxPool2d:
        return nn.MaxPool2d(
            kernel_size=layer.kernel_size,
            stride=layer.stride if hasattr(layer, 'stride') else None,
            # padding=getattr(layer, 'padding', 0)
        )

    def _build_avgpool2d(self, layer: Layer) -> nn.AvgPool2d:
        return nn.AvgPool2d(
            kernel_size=layer.kernel_size,
            stride=layer.stride if hasattr(layer, 'stride') else None,
            # padding=getattr(layer, 'padding', 0)
        )

    # Padding Layers
    def _build_reflection_pad2d(self, layer: Layer) -> nn.ReflectionPad2d:
        return nn.ReflectionPad2d(
            padding=layer.padding
        )

    def _build_replication_pad2d(self, layer: Layer) -> nn.ReplicationPad2d:
        return nn.ReplicationPad2d(
            padding=layer.padding
        )

    def _build_zero_pad2d(self, layer: Layer) -> nn.ZeroPad2d:
        return nn.ZeroPad2d(padding=layer.padding)

    def _build_constant_pad2d(self, layer: Layer) -> nn.ConstantPad2d:
        return nn.ConstantPad2d(padding=layer.padding, value=layer.value)

    # Activation Layers
    def _build_relu(self, layer: Layer) -> nn.ReLU:
        return nn.ReLU(inplace=getattr(layer, 'inplace', False))

    def _build_leaky_relu(self, layer: Layer) -> nn.LeakyReLU:
        return nn.LeakyReLU(
            negative_slope=layer.negative_slope,
            # inplace=getattr(layer, 'inplace', False)
        )

    def _build_elu(self, layer: Layer) -> nn.ELU:
        return nn.ELU(
            alpha=layer.alpha,
            # inplace=getattr(layer, 'inplace', False)
        )

    def _build_prelu(self, layer: Layer) -> nn.PReLU:
        return nn.PReLU(
            num_parameters=layer.num_parameters,
            init=layer.init
        )

    def _build_sigmoid(self, layer: Layer) -> nn.Sigmoid:
        return nn.Sigmoid()

    def _build_tanh(self, layer: Layer) -> nn.Tanh:
        return nn.Tanh()

    def _build_softmax(self, layer: Layer) -> nn.Softmax:
        return nn.Softmax(dim=layer.dim)

    def _build_log_softmax(self, layer: Layer) -> nn.LogSoftmax:
        return nn.LogSoftmax(dim=layer.dim)

    def _build_gelu(self, layer: Layer) -> nn.GELU:
        return nn.GELU()

    # Linear Layers
    def _build_linear(self, layer: Layer) -> nn.Linear:
        return nn.Linear(
            in_features=layer.in_features,
            out_features=layer.out_features,
            # bias=getattr(layer, 'bias', True)
        )

    def _build_flatten(self, layer: Layer) -> nn.Flatten:
        return nn.Flatten()