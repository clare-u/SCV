import pytest
import torch
import torch.nn as nn
from neural_network_builder.builders.model_builder import ModelBuilder  # 하이픈(-) 대신 언더스코어(_) 사용
from neural_network_builder.exceptions.custom_exceptions import BuilderError
from pathlib import Path


@pytest.fixture
def model_builder():
    return ModelBuilder()


@pytest.fixture
def sample_config():
    return {
        "model": [
            {
                "name": "Conv2d",
                "in_channels": 1,  # MNIST는 흑백 이미지므로 채널 1
                "out_channels": 64,
                "kernel_size": 3,
                "stride": 1,
                "padding": 1  # padding=1이므로 spatial dimension 유지
            },
            {
                "name": "ReLU"
            },
            {
                "name": "Flatten"
            },
            {
                "name": "Linear",
                "in_features": 64 * 28 * 28,
                "out_features": 10
            }
        ]
    }


@pytest.fixture
def config_file(tmp_path, sample_config):
    import json
    config_path = tmp_path / "test_config.json"
    config_path.write_text(json.dumps(sample_config))
    return str(config_path)


def test_build_from_json(model_builder, sample_config):
    model = model_builder.build_from_json(sample_config)
    assert isinstance(model, nn.Sequential)
    assert len(model) == 4
    assert isinstance(model[0], nn.Conv2d)
    assert isinstance(model[1], nn.ReLU)
    assert isinstance(model[2], nn.Flatten)
    assert isinstance(model[3], nn.Linear)


def test_build_from_file(model_builder, config_file):
    model = model_builder.build_from_file(config_file)
    assert isinstance(model, nn.Sequential)
    assert len(model) == 4


def test_build_invalid_json(model_builder):
    with pytest.raises(BuilderError):
        model_builder.build_from_json("invalid_json")


def test_build_invalid_file(model_builder):
    with pytest.raises(BuilderError):
        model_builder.build_from_file("non_existent_file.json")


def test_model_forward_pass(model_builder, sample_config):
    model = model_builder.build_from_json(sample_config)

    # Create dummy input tensor
    batch_size = 1
    channels = 1
    height = 28
    width = 28
    x = torch.randn(batch_size, channels, height, width)

    try:
        output = model(x)
        assert output.shape[1] == 10  # output features should match final layer
    except Exception as e:
        pytest.fail(f"Forward pass failed: {str(e)}")


def test_save_and_load_model(model_builder, sample_config, tmp_path):
    # Build and save model
    model = model_builder.build_from_json(sample_config)
    save_path = tmp_path / "test_model.pth"
    model_builder.save_model(model, str(save_path))

    # Verify file exists
    assert save_path.exists()

    # Load and verify
    try:
        state_dict = torch.load(str(save_path))
        new_model = model_builder.build_from_json(sample_config)
        new_model.load_state_dict(state_dict)
    except Exception as e:
        pytest.fail(f"Model loading failed: {str(e)}")