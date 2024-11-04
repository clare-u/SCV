import pytest
import torch.nn as nn
from neural_network_builder.builders.layer_builder import LayerBuilder
from neural_network_builder.parsers.validators import Conv2d, ReLU, Linear, MaxPool2d
from neural_network_builder.exceptions.custom_exceptions import BuilderError
from pydantic import BaseModel

class InvalidLayer(BaseModel):
    name: str = "InvalidLayer"

@pytest.fixture
def layer_builder():
    return LayerBuilder()


def test_build_conv2d(layer_builder):
    layer_config = Conv2d(
        name="Conv2d",
        in_channels=3,
        out_channels=64,
        kernel_size=3
    )
    layer = layer_builder.build(layer_config)
    assert isinstance(layer, nn.Conv2d)
    assert layer.in_channels == 3
    assert layer.out_channels == 64
    assert layer.kernel_size == (3, 3)


def test_build_relu(layer_builder):
    layer_config = ReLU(name="ReLU")
    layer = layer_builder.build(layer_config)
    assert isinstance(layer, nn.ReLU)


def test_build_linear(layer_builder):
    layer_config = Linear(
        name="Linear",
        in_features=64,
        out_features=10
    )
    layer = layer_builder.build(layer_config)
    assert isinstance(layer, nn.Linear)
    assert layer.in_features == 64
    assert layer.out_features == 10


def test_build_maxpool(layer_builder):
    layer_config = MaxPool2d(
        name="MaxPool2d",
        kernel_size=2,
        stride=2
    )
    layer = layer_builder.build(layer_config)
    assert isinstance(layer, nn.MaxPool2d)
    assert layer.kernel_size == 2
    assert layer.stride == 2


def test_build_invalid_layer(layer_builder):
    class InvalidLayer(BaseModel):
        name:str = "InvalidLayer"

    with pytest.raises(BuilderError):
        layer_builder.build(InvalidLayer())