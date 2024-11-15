import logging
import os
import sys
from pathlib import Path
from typing import Dict, Tuple, Any, Union

import torch
import yaml
from pydantic import ValidationError as PydanticValidationError
from torch import nn

# 상위 경로 추가
current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(os.path.dirname(current_dir))
sys.path.append(root_dir)

# model_test의 exceptions 모듈 import
from neural_network_builder.exceptions.custom_exceptions import (
    ValidationError, InputShapeError, ArchitectureError, LayerConnectionError
)
from neural_network_builder.parsers.validators import ModelConfig

def get_device():
    if torch.cuda.is_available():
        return torch.device("cuda")
    elif hasattr(torch.backends, "mps") and torch.backends.mps.is_available():
        return torch.device("mps")
    else:
        return torch.device("cpu")

class ModelValidator:
    """신경망 모델의 구조와 입력 데이터를 검증하는 클래스"""

    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.datasets_config = self._load_dataset_config()
        # self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.device = get_device()
        print(f"ModelValidator클래스 device종류: {self.device}")

    def _load_dataset_config(self) -> Dict:
        """데이터셋 설정 파일을 로드합니다."""
        try:
            config_path = Path(current_dir).parent / 'datasets' / 'configs' / 'datasets.yaml'
            with open(config_path, 'r') as f:
                return yaml.safe_load(f)['datasets']
        except Exception as e:
            raise ValidationError(f"데이터셋 설정 파일 로드 실패: {str(e)}")

    def _parse_model_config(self, config: Union[str, dict]) -> Any:
        """Parse and validate model configuration using Pydantic."""
        try:
            if isinstance(config, dict):
                return ModelConfig.model_validate(config)
            elif isinstance(config, str):
                return ModelConfig.model_validate_json(config)
            else:
                raise ValidationError(f"지원되지 않는 설정 형식: {type(config)}")
        except Exception as e:
            raise ValidationError(f"설정 파싱 실패: {str(e)}")

    def validate_model_config(self, config: Union[str, dict]) -> bool:
        """Pydantic 모델 설정을 검증합니다."""
        try:
            config = self._parse_model_config(config)
            return True
        except PydanticValidationError as e:
            raise ValidationError(f"모델 설정 검증 실패: {str(e)}")
        except Exception as e:
            raise ValidationError(f"모델 설정 검증 중 예상치 못한 오류: {str(e)}")

    def validate_input_shape(self, dataset_name: str, model_input_shape: Tuple) -> bool:
        """모델의 입력 shape이 데이터셋과 일치하는지 검증합니다."""
        if dataset_name not in self.datasets_config:
            raise ValidationError(f"지원되지 않는 데이터셋: {dataset_name}")

        expected_shape = tuple(self.datasets_config[dataset_name]['input_shape'])
        if model_input_shape != expected_shape:
            raise InputShapeError(expected_shape, model_input_shape)

        return True

    def create_test_batch(self, dataset_name: str, batch_size: int = 1) -> torch.Tensor:
        """데이터셋 설정에 맞는 테스트용 배치를 생성합니다."""
        if dataset_name not in self.datasets_config:
            raise ValidationError(f"지원되지 않는 데이터셋: {dataset_name}")

        input_shape = self.datasets_config[dataset_name]['input_shape']
        test_shape = (batch_size, *input_shape)

        # 데이터셋의 정규화 파라미터를 적용
        mean = torch.tensor(self.datasets_config[dataset_name]['mean'])
        std = torch.tensor(self.datasets_config[dataset_name]['std'])

        # 정규화된 랜덤 데이터 생성
        random_data = torch.rand(test_shape)
        normalized_data = (random_data - mean.view(-1, 1, 1)) / std.view(-1, 1, 1)
        print(f"생성된 test batch device: {normalized_data.device}")

        return normalized_data.to(self.device)

    def validate_model(self, model: torch.nn.Module, dataset_name: str) -> Dict[str, Any]:
        """모델의 전체적인 검증을 수행합니다."""
        try:
            print(f"검증 시작 시 설정된 device: {self.device}")

            # 모델을 평가 모드로 설정
            model.eval()

            # 테스트 배치 생성
            model = model.to(self.device)

            # 모든 파라미터가 같은 device에 있는지 확인
            for param in model.parameters():
                if param.device != self.device:
                    param.data = param.data.to(self.device)

            test_batch = self.create_test_batch(dataset_name).to(self.device)

            # 디버깅용
            print(f"모델의device종류: {next(model.parameters()).device}")
            print(f"Testbatch device종류: {test_batch.device}")            # test_batch = test_batch.to(next(model.parameters()).device)

            # device 일치 검증
            model_device = next(model.parameters()).device
            if test_batch.device != model_device:
                if test_batch.device != model_device:
                    test_batch = test_batch.to(model_device)
                    print(f"Test batch를 model device({model_device})로 이동시킴")

            # Forward pass 시도
            with torch.no_grad():
                output = model(test_batch)

            # 출력 검증
            expected_classes = self.datasets_config[dataset_name]['num_classes']
            if output.size(-1) != expected_classes:
                raise ArchitectureError(
                    f"출력 클래스 수 불일치 - 예상: {expected_classes}, 실제: {output.size(-1)}"
                )

            return {
                "status": "success",
                "input_shape": tuple(test_batch.shape[1:]),
                "output_shape": tuple(output.shape),
                "num_classes": expected_classes
            }

        except Exception as e:
            self.logger.error(f"모델 검증 실패: {str(e)}")
            raise

    def check_layer_connections(self, model: torch.nn.Module, dataset_name: str) -> None:
        """모델의 레이어 간 연결을 검증합니다."""
        model = model.to(self.device)
        test_batch = self.create_test_batch(dataset_name)
        # test_batch = test_batch.to(next(model.parameters()).device)

        activation = {}
        hooks = []

        def hook_fn(name):
            def hook(module, input, output):
                activation[name] = {
                    'shape': output.shape,
                    'layer_type': type(module).__name__
                }

            return hook

        # 주요 레이어에만 hook 추가
        for name, layer in model.named_modules():
            if isinstance(layer, (nn.Conv2d, nn.MaxPool2d, nn.Linear, nn.Flatten)):
                hooks.append(layer.register_forward_hook(hook_fn(name)))

        try:
            # Forward pass
            with torch.no_grad():
                model(test_batch)

            # 연결 검증
            shapes = list(activation.items())
            for i in range(len(shapes) - 1):
                curr_name, curr_info = shapes[i]
                next_name, next_info = shapes[i + 1]

                curr_shape = curr_info['shape']
                next_shape = next_info['shape']
                curr_type = curr_info['layer_type']
                next_type = next_info['layer_type']

                # # Conv2d -> MaxPool2d 연결 검증
                # if curr_type == 'Conv2d' and next_type == 'MaxPool2d':
                #     if curr_shape[2] % 2 != 0 or curr_shape[3] % 2 != 0:
                #         error_msg = (
                #             f"Conv2d의 출력 크기({curr_shape[2]}x{curr_shape[3]})가 "
                #             f"MaxPool2d의 stride(2)로 나누어 떨어지지 않습니다."
                #         )
                #         raise LayerConnectionError(curr_name, curr_shape, next_shape, error_msg)

                # MaxPool2d -> Flatten 연결 검증
                if curr_type == 'MaxPool2d' and next_type == 'Flatten':
                    expected_flatten_size = curr_shape[1] * curr_shape[2] * curr_shape[3]
                    if next_shape[1] != expected_flatten_size:
                        error_msg = (
                            f"Flatten 후의 크기가 예상값과 다릅니다. "
                            f"예상: {expected_flatten_size}, 실제: {next_shape[1]}"
                        )
                        raise LayerConnectionError(curr_name, curr_shape, next_shape, error_msg)

                # Flatten -> Linear 연결 검증
                elif curr_type == 'Flatten' and next_type == 'Linear':
                    if curr_shape[1] != model._modules[next_name].in_features:
                        error_msg = (
                            f"Flatten 출력({curr_shape[1]})이 Linear layer의 "
                            f"입력 크기({model._modules[next_name].in_features})와 맞지 않습니다."
                        )
                        raise LayerConnectionError(curr_name, curr_shape, next_shape, error_msg)

                # Conv2d -> Conv2d 연결 검증
                elif curr_type == 'Conv2d' and next_type == 'Conv2d':
                    if curr_shape[1] != model._modules[next_name].in_channels:
                        error_msg = (
                            f"Conv2d 레이어 간 채널 수가 맞지 않습니다. "
                            f"현재 출력: {curr_shape[1]}, 다음 입력: {model._modules[next_name].in_channels}"
                        )
                        raise LayerConnectionError(curr_name, curr_shape, next_shape, error_msg)

                # Linear -> Linear 연결 검증
                elif curr_type == 'Linear' and next_type == 'Linear':
                    if curr_shape[1] != model._modules[next_name].in_features:
                        error_msg = (
                            f"Linear 레이어 간 특성 수가 맞지 않습니다. "
                            f"현재 출력: {curr_shape[1]}, 다음 입력: {model._modules[next_name].in_features}"
                        )
                        raise LayerConnectionError(curr_name, curr_shape, next_shape, error_msg)

        except Exception as e:
            # Hook 정리
            for hook in hooks:
                hook.remove()
            raise
        finally:
            # Hook 정리
            for hook in hooks:
                hook.remove()

    def _is_compatible_shape(self, shape1: Tuple, shape2: Tuple) -> bool:
        """두 shape의 호환성을 검사합니다."""
        # Conv나 Pool 레이어의 경우 (batch_size, channels, height, width)
        if len(shape1) == 4 and len(shape2) == 4:
            # 채널 수가 같아야 함
            if shape1[1] != shape2[1]:
                return False

            # height와 width는 2배까지 차이 허용
            h_ratio = shape1[2] / shape2[2]
            w_ratio = shape1[3] / shape2[3]
            return 0.45 <= h_ratio <= 2.2 and 0.45 <= w_ratio <= 2.2

        # 다른 레이어들의 경우 정확한 크기 매칭 필요
        if len(shape1) != len(shape2):
            return False
        return all(s1 == s2 for s1, s2 in zip(shape1[1:], shape2[1:]))
