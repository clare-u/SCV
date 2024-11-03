import torch
import torch.nn as nn
from typing import List, Union, Dict
from ..parsers.json_parser import JSONParser
from ..parsers.validators import Layer
from .layer_builder import LayerBuilder
from ..utils.logger import setup_logger
from ..exceptions.custom_exceptions import BuilderError

logger = setup_logger(__name__)


class ModelBuilder:
    """Neural network model builder"""

    def __init__(self):
        self.layer_builder = LayerBuilder()
        self.json_parser = JSONParser()

    def build_from_json(self, json_config: Union[str, Dict]) -> nn.Sequential:
        """JSON 설정으로부터 PyTorch 모델을 생성합니다."""
        try:
            # JSON 파싱
            layers = self.json_parser.parse_config(json_config)
            return self.build_from_layers(layers)

        except Exception as e:
            logger.error(f"Error building model from JSON: {str(e)}")
            raise BuilderError(f"Failed to build model from JSON: {str(e)}")

    def build_from_file(self, config_file: str) -> nn.Sequential:
        """JSON 파일로부터 PyTorch 모델을 생성합니다."""
        try:
            # JSON 파일 파싱
            layers = self.json_parser.parse_file(config_file)
            return self.build_from_layers(layers)

        except Exception as e:
            logger.error(f"Error building model from file: {str(e)}")
            raise BuilderError(f"Failed to build model from file: {str(e)}")

    def build_from_layers(self, layers: List[Layer]) -> nn.Sequential:
        """Layer 객체 리스트로부터 PyTorch 모델을 생성합니다."""
        try:
            # 각 레이어 생성
            torch_layers = []
            for i, layer in enumerate(layers):
                try:
                    torch_layer = self.layer_builder.build(layer)
                    torch_layers.append(torch_layer)
                    logger.debug(f"Built layer {i}: {layer.name}")
                except Exception as e:
                    raise BuilderError(f"Error building layer {i} ({layer.name}): {str(e)}")

            # Sequential 모델 생성
            model = nn.Sequential(*torch_layers)
            logger.info(f"Successfully built model with {len(torch_layers)} layers")
            return model

        except Exception as e:
            logger.error(f"Error building model: {str(e)}")
            raise BuilderError(f"Failed to build model: {str(e)}")

    def save_model(self, model: nn.Sequential, file_path: str):
        """생성된 모델을 파일로 저장합니다."""
        try:
            torch.save(model.state_dict(), file_path)
            logger.info(f"Model saved to {file_path}")
        except Exception as e:
            logger.error(f"Error saving model: {str(e)}")
            raise BuilderError(f"Failed to save model: {str(e)}")