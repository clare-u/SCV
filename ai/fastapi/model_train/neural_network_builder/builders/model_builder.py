"""
전체 신경망 모델을 생성하는 빌더 클래스
JSONParser와 LayerBuilder를 조합하여 사용
JSON 설정 -> PyTorch 모델 생성
모델 저장 기능 제공
"""
from functools import wraps
from typing import List, Union, Dict

import torch
import torch.nn as nn

from .layer_builder import LayerBuilder
from exceptions.custom_exceptions import BuilderError
from parsers.json_parser import JSONParser
from parsers.validators import Layer, Flatten
from utils.logger import setup_logger

logger = setup_logger(__name__)


def handle_errors(func):
    """에러 처리 및 로깅을 위한 데코레이터(반복되는 try-except부분을 유지보수 높이기 위한 함수)"""

    @wraps(func)
    def wrapper(*args, **kwargs):
        try:
            return func(*args, **kwargs)
        except Exception as e:
            func_name = func.__name__
            error_msg = f"Failed in {func_name}: {str(e)}"
            logger.error(error_msg)
            raise BuilderError(error_msg)

    return wrapper


class ModelBuilder:
    """신경망 모델 빌더"""

    def __init__(self):
        self.layer_builder = LayerBuilder()
        self.json_parser = JSONParser()

    @handle_errors
    def create_model(self, json_config: Union[str, Dict]) -> nn.Sequential:
        """JSON 설정으로부터 PyTorch 모델 생성"""
        try:
            # JSON 파싱
            layers = self.json_parser.parse_config(json_config)
            if not layers:
                raise BuilderError("레이어 리스트가 비어있습니다")

            # flatten레이어 자동 삽입
            layers = self._insert_flatten_layer(layers)

            logger.debug(f"Parsed layers: {[layer.model_dump() for layer in layers]}")
            return self._build_model(layers)

        except Exception as e:
            logger.error(f"모델 생성 중 오류 발생: {str(e)}")
            raise BuilderError(f"모델 생성 실패: {str(e)}")

    def _is_2d_operation_layer(self, layer_name: str) -> bool:
        """2D 연산 수행하는 레이어인지 확인"""
        return layer_name in [
            "Conv2d", "ConvTranspose2d",
            "MaxPool2d", "AvgPool2d",
            "ReflectionPad2d", "ReplicationPad2d",
            "ZeroPad2d", "ConstantPad2d",
        ]

    def _insert_flatten_layer(self, layers: List[Layer]) -> List[Layer]:
        """첫 linear레이어 앞에 flatten레이어 자동 삽입"""
        processed_layers = []
        found_first_linear = False
        has_2d_operation = False

        for layer in layers:
            # 2D 연산 레이어 확인
            if self._is_2d_operation_layer(layer.name):
                has_2d_operation = True

            # 첫 linear레이어 발견시
            if layer.name == "Linear" and not found_first_linear:
                found_first_linear = True

                # 이전에 2D연산 여부 True고 직전 레이어가 Flatten이 아닌 경우만 삽입
                if has_2d_operation and (not processed_layers or processed_layers[-1].name != "Flatten"):
                    flatten_layer = Flatten(name="Flatten")
                    processed_layers.append(flatten_layer)
                    logger.info("첫번쨰 Linear레이어 전에 Flatten레이어 자동삽입성공")

            processed_layers.append(layer)

        return processed_layers

    @handle_errors
    def _build_model(self, layers: List[Layer]) -> nn.Sequential:
        """Layer 객체 리스트로부터 PyTorch 모델 생성"""
        # 각 레이어 생성
        torch_layers = []
        for i, layer in enumerate(layers):
            torch_layer = self.layer_builder.build(layer)
            torch_layers.append(torch_layer)
            logger.debug(f"Built layer {i}: {layer.name}")

        # Sequential 모델 생성
        model = nn.Sequential(*torch_layers)
        logger.info(f"{len(torch_layers)}개의 레이어로 모델을 성공적으로 구축했습니다.")
        return model

    @handle_errors
    def save_model(self, model: nn.Sequential, file_path: str):
        """생성된 모델을 파일로 저장"""
        torch.save(model.state_dict(), file_path)
        logger.info(f"Model saved to {file_path}")
