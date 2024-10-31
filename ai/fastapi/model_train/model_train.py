import json
import torch.nn as nn


class ModelBuilder(nn.Module):
    def __init__(self, config_json):
        super(ModelBuilder, self).__init__()

        # JSON 문자열을 파이썬 딕셔너리로 변환
        if isinstance(config_json, str):
            config = json.loads(config_json)
        else:
            config = config_json

        # 레이어 리스트 가져오기
        self.layer_configs = config['model']

        # 레이어들을 저장할 ModuleList
        self.layers = nn.ModuleList()

        # JSON 설정에 따라 레이어 생성
        for layer_config in self.layer_configs:
            layer = self._create_layer(layer_config)
            if layer is not None:
                self.layers.append(layer)

    def _create_layer(self, layer_config):
        """레이어 설정에 따라 적절한 PyTorch 레이어를 생성"""
        layer_type = layer_config['name'].lower()

        # Convolution Layers
        if layer_type == 'conv2d':
            return nn.Conv2d(
                in_channels=layer_config['in_channels'],
                out_channels=layer_config['out_channels'],
                kernel_size=layer_config['kernel_size'],
                stride=layer_config.get('stride', 1),
                padding=layer_config.get('padding', 0)
            )
        elif layer_type == 'convtranspose2d':
            return nn.ConvTranspose2d(
                in_channels=layer_config['in_channels'],
                out_channels=layer_config['out_channels'],
                kernel_size=layer_config['kernel_size'],
                stride=layer_config.get('stride', 1),
                padding=layer_config.get('padding', 0)
            )

        # Pooling Layers
        elif layer_type == 'maxpool2d':
            return nn.MaxPool2d(
                kernel_size=layer_config['kernel_size'],
                stride=layer_config.get('stride', None)
            )
        elif layer_type == 'avgpool2d':
            return nn.AvgPool2d(
                kernel_size=layer_config['kernel_size'],
                stride=layer_config.get('stride', None)
            )

        # Padding Layers
        elif layer_type == 'reflectionpad2d':
            return nn.ReflectionPad2d(padding=layer_config['padding'])
        elif layer_type == 'replicationpad2d':
            return nn.ReplicationPad2d(padding=layer_config['padding'])
        elif layer_type == 'zeropad2d':
            return nn.ZeroPad2d(padding=layer_config['padding'])
        elif layer_type == 'constantpad2d':
            return nn.ConstantPad2d(
                padding=layer_config['padding'],
                value=layer_config['value']
            )

        # Non-Linear Activations
        elif layer_type == 'relu':
            return nn.ReLU()
        elif layer_type == 'leakyrelu':
            return nn.LeakyReLU(negative_slope=layer_config.get('negative_slope', 0.01))
        elif layer_type == 'elu':
            return nn.ELU(alpha=layer_config.get('alpha', 1.0))
        elif layer_type == 'prelu':
            return nn.PReLU(
                num_parameters=layer_config.get('num_parameters', 1),
                init=layer_config.get('init', 0.25)
            )
        elif layer_type == 'sigmoid':
            return nn.Sigmoid()
        elif layer_type == 'tanh':
            return nn.Tanh()
        elif layer_type == 'softmax':
            return nn.Softmax(dim=layer_config['dim'])
        elif layer_type == 'logsoftmax':
            return nn.LogSoftmax(dim=layer_config['dim'])
        elif layer_type == 'gelu':
            return nn.GELU()

        # Linear Layers
        elif layer_type == 'linear':
            return nn.Linear(
                in_features=layer_config['in_features'],
                out_features=layer_config['out_features']
            )

        else:
            raise ValueError(f"Unsupported layer type: {layer_type}")

    def forward(self, x):
        """순전파 수행"""
        for layer in self.layers:
            x = layer(x)
        return x


# 사용 예시
if __name__ == "__main__":
    # 예시 설정
    config = {
        "model": [
            {
                "name": "conv2d",
                "in_channels": 1,
                "out_channels": 16,
                "kernel_size": 3
            },
            {
                "name": "relu"
            },
            {
                "name": "maxpool2d",
                "kernel_size": 2,
                "stride": 2
            },
            {
                "name": "linear",
                "in_features": 1024,
                "out_features": 10
            }
        ]
    }

    # 모델 생성
    model = ModelBuilder(config)
    print(model)