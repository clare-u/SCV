"""
JSON 형식의 모델 구성을 파싱하는 클래스
JSON -> Layer 객체 변환 및 검증
파일 입출력 기능 제공
에러 처리 및 로깅
"""
import json
from typing import Dict, List, Union, Any
from ..utils.logger import setup_logger
from ..exceptions.custom_exceptions import JSONParsingError, LayerConfigError, UnsupportedLayerError
from .validators import ModelConfig, layer_classes, Layer

logger = setup_logger(__name__)


class JSONParser:
    """신경망 모델 JSON 구성 parser"""

    @staticmethod
    def validate_layer_name(layer_config: Dict[str, Any]) -> str:
        """레이어 이름을 검증 및 반환"""
        try:
            layer_name = layer_config.get('name')
            if not layer_name:
                raise LayerConfigError("레이어 이름이 없습니다!")

            if layer_name not in layer_classes:
                raise UnsupportedLayerError(layer_name)

            return layer_name

        except Exception as e:
            logger.error(f"레이어 이름 검증 중 오류 발생!: {str(e)}")
            raise

    @classmethod
    def parse_config(cls, config_data: Union[str, Dict[str, Any]]) -> List[Layer]:
        """JSON 설정을 파싱하여 Layer 객체 리스트 반환"""
        try:
            # JSON 문자열을 딕셔너리로 변환
            if isinstance(config_data, str):
                try:
                    config_dict = json.loads(config_data)
                except json.JSONDecodeError as e:
                    raise JSONParsingError(f"잘못된 JSON 형식입니다!: {str(e)}", config_data)
            else:
                config_dict = config_data

            # 디버깅용 config 출력
            logger.debug(f"설정: {config_dict}")

            # ModelConfig를 사용하여 전체 구조 검증 및 변환
            try:
                model_config = ModelConfig(**config_dict)

                # 검증된 레이어 리스트 반환
                layers = model_config.modelLayerAt.layers

                logger.info(f"총 {len(layers)}개의 레이어를 성공적으로 파싱했습니다")
                for i, layer in enumerate(layers):
                    logger.debug(f"레이어 {i}: {layer.model_dump()}")

                return layers

            except Exception as e:
                logger.error(f"ModelConfig validation error: {str(e)}")
                raise LayerConfigError(f"잘못된 레이어 설정입니다: {str(e)}")

        except Exception as e:
            logger.error(f"설정 파싱 중 오류 발생: {str(e)}")
            raise

    @classmethod
    def parse_file(cls, file_path: str) -> List[Layer]:
        """JSON 파일을 읽어서 Layer 객체 리스트 반환"""
        try:
            with open(file_path, 'r') as f:
                config_data = f.read()
            return cls.parse_config(config_data)

        except FileNotFoundError:
            logger.error(f"설정 파일을 찾을 수 없습니다!: {file_path}")
            raise
        except Exception as e:
            logger.error(f"설정 파일 읽기 중 오류 발생!: {str(e)}")
            raise

    @classmethod
    def to_json(cls, layers: List[Layer]) -> str:
        """Layer 객체 리스트를 JSON 문자열로 변환합니다."""
        try:
            # Pydantic 모델의 model_dump() 메소드 사용
            layer_dicts = [layer.model_dump() for layer in layers]
            return json.dumps({"modelLayerAt": {"layers": layer_dicts}}, indent=2)
        except Exception as e:
            logger.error(f"레이어를 JSON으로 변환 중 오류 발생: {str(e)}")
            raise JSONParsingError("레이어를 JSON으로 변환하는데 실패했습니다", str(e))

    @classmethod
    def validate_json(cls, json_data: Union[str, Dict[str, Any]]) -> bool:
        """JSON 설정의 유효성을 검사합니다."""
        try:
            cls.parse_config(json_data)
            return True
        except Exception as e:
            logger.warning(f"JSON 유효성 검사 실패: {str(e)}")
            return False
