import pytest
import json
from neural_network_builder.parsers.json_parser import JSONParser
from neural_network_builder.exceptions.custom_exceptions import JSONParsingError, LayerConfigError
from pathlib import Path

@pytest.fixture
def sample_json():
    return {
        "model": [
            {
                "name": "Conv2d",
                "in_channels": 3,
                "out_channels": 64,
                "kernel_size": 3
            },
            {
                "name": "ReLU"
            }
        ]
    }

@pytest.fixture
def sample_json_file(tmp_path):
    json_file = tmp_path / "test_model.json"
    config = {
        "model": [
            {
                "name": "Conv2d",
                "in_channels": 3,
                "out_channels": 64,
                "kernel_size": 3
            }
        ]
    }
    json_file.write_text(json.dumps(config))
    return str(json_file)

def test_parse_config_valid_json(sample_json):
    parser = JSONParser()
    layers = parser.parse_config(sample_json)
    assert len(layers) == 2
    assert layers[0].name == "Conv2d"
    assert layers[1].name == "ReLU"

def test_parse_config_invalid_json():
    parser = JSONParser()
    with pytest.raises(JSONParsingError):
        parser.parse_config("{invalid_json")

def test_parse_config_missing_model_key():
    parser = JSONParser()
    with pytest.raises(JSONParsingError):
        parser.parse_config({})

def test_parse_file_valid(sample_json_file):
    parser = JSONParser()
    layers = parser.parse_file(sample_json_file)
    assert len(layers) == 1
    assert layers[0].name == "Conv2d"

def test_parse_file_not_found():
    parser = JSONParser()
    with pytest.raises(FileNotFoundError):
        parser.parse_file("non_existent_file.json")

def test_validate_json_valid(sample_json):
    parser = JSONParser()
    assert parser.validate_json(sample_json) is True

def test_validate_json_invalid():
    parser = JSONParser()
    assert parser.validate_json("invalid_json") is False