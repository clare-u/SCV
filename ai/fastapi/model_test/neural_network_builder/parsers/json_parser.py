import json
from typing import Dict, List, Union, Any
from ..utils.logger import setup_logger
from ..exceptions.custom_exceptions import JSONParsingError, LayerConfigError, UnsupportedLayerError
from .validators import ModelConfig, layer_classes, Layer

logger = setup_logger(__name__)


class JSONParser:
    """Neural network model JSON configuration parser"""

    @staticmethod
    def validate_layer_name(layer_config: Dict[str, Any]) -> str:
        """레이어 이름을 검증하고 반환합니다."""
        try:
            layer_name = layer_config.get('name')
            if not layer_name:
                raise LayerConfigError("Layer name is missing")

            if layer_name not in layer_classes:
                raise UnsupportedLayerError(layer_name)

            return layer_name

        except Exception as e:
            logger.error(f"Error validating layer name: {str(e)}")
            raise

    @classmethod
    def parse_config(cls, config_data: Union[str, Dict[str, Any]]) -> List[Layer]:
        """JSON 설정을 파싱하여 Layer 객체 리스트를 반환합니다."""
        try:
            # JSON 문자열을 딕셔너리로 변환
            if isinstance(config_data, str):
                try:
                    config_dict = json.loads(config_data)
                except json.JSONDecodeError as e:
                    raise JSONParsingError(f"Invalid JSON format: {str(e)}", config_data)
            else:
                config_dict = config_data

            # 기본 구조 검증
            if not isinstance(config_dict, dict) or 'model' not in config_dict:
                raise JSONParsingError("Invalid configuration format: 'model' key is required", str(config_dict))

            # ModelConfig를 사용하여 전체 구조 검증 및 변환
            try:
                model_config = ModelConfig(model=config_dict['model'])
                layers = model_config.model

                # 로깅
                logger.info(f"Successfully parsed {len(layers)} layers")
                for i, layer in enumerate(layers):
                    logger.debug(f"Layer {i}: {layer.name}")

                return layers

            except Exception as e:
                raise LayerConfigError(f"Invalid layer configuration: {str(e)}")

        except Exception as e:
            logger.error(f"Error parsing configuration: {str(e)}")
            raise

    @classmethod
    def parse_file(cls, file_path: str) -> List[Layer]:
        """JSON 파일을 읽어서 Layer 객체 리스트를 반환합니다."""
        try:
            with open(file_path, 'r') as f:
                config_data = f.read()
            return cls.parse_config(config_data)

        except FileNotFoundError:
            logger.error(f"Configuration file not found: {file_path}")
            raise
        except Exception as e:
            logger.error(f"Error reading configuration file: {str(e)}")
            raise

    @classmethod
    def to_json(cls, layers: List[Layer]) -> str:
        """Layer 객체 리스트를 JSON 문자열로 변환합니다."""
        try:
            layer_dicts = [layer.model_dump() for layer in layers]
            config_dict = {'model': layer_dicts}
            return json.dumps(config_dict, indent=2)

        except Exception as e:
            logger.error(f"Error converting layers to JSON: {str(e)}")
            raise JSONParsingError("Failed to convert layers to JSON", str(e))

    @classmethod
    def validate_json(cls, json_data: Union[str, Dict[str, Any]]) -> bool:
        """JSON 설정의 유효성을 검사합니다."""
        try:
            cls.parse_config(json_data)
            return True
        except Exception as e:
            logger.warning(f"JSON validation failed: {str(e)}")
            return False