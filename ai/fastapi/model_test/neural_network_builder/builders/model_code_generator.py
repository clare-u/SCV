"""
딥러닝 모델을 파이썬 코드로 변환하는 생성기
학습된 PyTorch 모델을 받아서 실행 가능한 파이썬 코드로 변환합니다.
"""
import os
import torch
import torch.nn as nn
from typing import List, Dict, Any, Optional
from dataclasses import dataclass


@dataclass
class LayerInfo:
    """레이어 정보를 저장하는 데이터 클래스"""
    name: str
    params: Dict[str, Any]
    input_shape: Optional[tuple] = None
    output_shape: Optional[tuple] = None


class ModelCodeGenerator:
    """PyTorch 모델을 파이썬 코드로 변환하는 생성기"""

    def __init__(self):
        self.indent = "    "
        self.layer_imports = {
            "Conv2d": "nn.Conv2d",
            "ConvTranspose2d": "nn.ConvTranspose2d",

            "MaxPool2d": "nn.MaxPool2d",
            "AvgPool2d": "nn.AvgPool2d",

            "ReflectionPad2d": "nn.ReflectionPad2d",
            "ReplicationPad2d": "nn.ReplicationPad2d",
            "ZeroPad2d": "nn.ZeroPad2d",
            "ConstantPad2d": "nn.ConstantPad2d",

            "ReLU": "nn.ReLU",
            "LeakyReLU": "nn.LeakyReLU",
            "ELU": "nn.ELU",
            "PReLU": "nn.PReLU",
            "Sigmoid": "nn.Sigmoid",
            "Tanh": "nn.Tanh",
            "Softmax": "nn.Softmax",
            "LogSoftmax": "nn.LogSoftmax",
            "GELU": "nn.GELU",

            "Linear": "nn.Linear",
            "Flatten": "nn.Flatten"
        }

    def extract_layer_info(self, model: nn.Module) -> List[LayerInfo]:
        """모델에서 각 레이어의 정보를 추출"""
        layer_infos = []

        for name, layer in model.named_children():
            params = {}
            layer_type = layer.__class__.__name__

            if isinstance(layer, (nn.Conv2d, nn.ConvTranspose2d)):
                params.update({
                    'in_channels': layer.in_channels,
                    'out_channels': layer.out_channels,
                    'kernel_size': layer.kernel_size,
                    'stride': layer.stride,
                    'padding': layer.padding,
                    'dilation': layer.dilation,
                    'groups': layer.groups,
                    'padding_mode': layer.padding_mode,
                    'bias': layer.bias is not None
                })
            elif isinstance(layer, (nn.MaxPool2d, nn.AvgPool2d)):
                params.update({
                    'kernel_size': layer.kernel_size,
                    'stride': layer.stride,
                    'padding': layer.padding
                })
            elif isinstance(layer, nn.Linear):
                params.update({
                    'in_features': layer.in_features,
                    'out_features': layer.out_features,
                    'bias': layer.bias is not None
                })
            elif isinstance(layer, nn.LeakyReLU):
                params['negative_slope'] = layer.negative_slope
            elif isinstance(layer, nn.ELU):
                params['alpha'] = layer.alpha
            elif isinstance(layer, nn.PReLU):
                params['num_parameters'] = layer.num_parameters
            elif isinstance(layer, (nn.Softmax, nn.LogSoftmax)):
                params['dim'] = layer.dim
            elif isinstance(layer, nn.Flatten):
                params.update({
                    'start_dim': layer.start_dim,
                    'end_dim': layer.end_dim
                })

            # None값과 기본값 제거
            params = {k: v for k, v in params.items() if v is not None}
            layer_infos.append(LayerInfo(name=layer_type, params=params))

        return layer_infos

    def format_param_value(self, value: Any) -> str:
        if isinstance(value, str):
            return f"'{value}'"
        elif isinstance(value, (tuple, list)):
            return str(value)
        elif isinstance(value, bool):
            return str(value)
        elif isinstance(value, (int, float)):
            return str(value)
        return str(value)

    def generate_model_code(self, model: nn.Module, version_no: str, dataset_info: dict) -> str:
        """모델 코드 생성

           Args:
               model: PyTorch 모델
               version_no: 모델 버전
               dataset_info: 데이터셋 정보

           Returns:
               str: 생성된 파이썬 코드
       """
        layer_infos = self.extract_layer_info(model)

        code_lines = [
            "import torch",
            "import torch.nn as nn",
            "",
            f"# 모델 버전: {version_no}",
            f"# 데이터셋: {dataset_info.get('dataName', 'Unknown')}",
            f"# 학습 데이터 수: {dataset_info.get('dataTrainCnt', 'Unknown')}",
            f"# 테스트 데이터 수: {dataset_info.get('dataTestCnt', 'Unknown')}",
            f"# 레이블 수: {dataset_info.get('dataLabelCnt', 'Unknown')}",
            f"# 에폭 수: {dataset_info.get('dataEpochCnt', 'Unknown')}",
            "",
            "class Model(nn.Module):",
            f"{self.indent}def __init__(self):",
            f"{self.indent}{self.indent}super().__init__()",
            ""
        ]

        # 레이어 정의 추가
        for idx, layer_info in enumerate(layer_infos):
            params_str = ", ".join([
                f"{k}={self.format_param_value(v)}"
                for k, v in layer_info.params.items()
            ])

            layer_class = self.layer_imports.get(layer_info.name, f"nn.{layer_info.name}")
            code_lines.append(f"{self.indent}{self.indent}self.layer{idx} = {layer_class}({params_str})")

        # forward method 추가
        code_lines.extend([
            "",
            f"{self.indent}def forward(self, x):",
            f"{self.indent}{self.indent}# Input shape: [batch_size, channels, height, width]"
        ])

        for idx in range(len(layer_infos)):
            code_lines.append(f"{self.indent}{self.indent}x = self.layer{idx}(x)")

        code_lines.append(f"{self.indent}{self.indent}return x")

        # 실행 예제 추가
        code_lines.extend([
            "",
            "if __name__ == '__main__':",
            f"{self.indent}# 모델 인스턴스 생성",
            f"{self.indent}model = Model()",
            f"{self.indent}print('모델 구조:')",
            f"{self.indent}print(model)",
            f"{self.indent}",
            f"{self.indent}# 입력 텐서 예제",
            f"{self.indent}batch_size = 1  # 배치 크기",
            f"{self.indent}channels = {dataset_info.get('input_channels', 1)}  # 입력 채널 수",
            f"{self.indent}height = {dataset_info.get('input_height', 28)}  # 입력 높이",
            f"{self.indent}width = {dataset_info.get('input_width', 28)}  # 입력 너비",
            f"{self.indent}x = torch.randn(batch_size, channels, height, width)",
            f"{self.indent}",
            f"{self.indent}# 순전파 실행",
            f"{self.indent}output = model(x)",
            f"{self.indent}print(f'입력 shape: {{x.shape}}')",
            f"{self.indent}print(f'출력 shape: {{output.shape}}')"
        ])

        return "\n".join(code_lines)

    def save_model_code(self, model: nn.Module, version_no: str, dataset_info: dict) -> str:
        """Generate and save model code"""
        code = self.generate_model_code(model, version_no, dataset_info)

        # Create models directory if it doesn't exist
        models_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "models")
        os.makedirs(models_dir, exist_ok=True)

        # Save the model code
        file_path = os.path.join(models_dir, f"model_v{version_no}.py")
        with open(file_path, "w") as f:
            f.write(code)

        return code