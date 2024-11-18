import { BlockCategory, BlockDefinition } from "@/types";

export const CustomBlockList: Record<BlockCategory, BlockDefinition[]> = {
  Basic: [
    { name: "start", params: [] },
    { name: "end", params: [] },
  ],
  Convolution: [
    {
      name: "nn.Conv2d",
      params: [
        { name: "in_channels", type: "int", min: 1, value: undefined },
        { name: "out_channels", type: "int", min: 1, value: undefined },
        { name: "kernel_size", type: "int", min: 1, value: undefined },
      ],
      tooltip:
        "2D 컨볼루션 레이어로, 이미지의 공간적 특징을 추출하기 위해 사용됩니다. 작은 필터(커널)를 사용하여 이미지의 작은 영역을 스캔하며, 패턴, 경계, 색상 등의 특징을 학습합니다. 일반적으로 필터의 개수가 많을수록 다양한 특징을 포착할 수 있으며, 모델의 깊이에 따라 추출되는 특징의 복잡성이 증가합니다.",
    },
    {
      name: "nn.ConvTranspose2d",
      params: [
        { name: "in_channels", type: "int", min: 1, value: undefined },
        { name: "out_channels", type: "int", min: 1, value: undefined },
        { name: "kernel_size", type: "int", min: 1, value: undefined },
      ],
      tooltip:
        "전치 컨볼루션 레이어로, 주로 업샘플링을 통해 이미지 크기를 증가시키는 데 사용됩니다. 일반적인 컨볼루션 레이어와 반대의 역할을 하며, 주로 이미지 복원, 생성, 또는 객체 감지에서의 객체 위치 복원에 활용됩니다.",
    },
  ],
  Pooling: [
    {
      name: "MaxPool2d",
      params: [
        { name: "kernel_size", type: "int", min: 1, value: undefined },
        { name: "stride", type: "int", min: 1, value: undefined },
      ],
      tooltip:
        "최대 풀링 레이어는 지정된 커널 영역 내에서 가장 큰 값을 선택하여 출력하는 다운샘플링 기법입니다. 이미지의 특징을 요약하는 역할을 하며, 불필요한 데이터나 잡음을 제거하여 중요한 특징만을 남기는 데 사용됩니다.",
    },
    {
      name: "AvgPool2d",
      params: [
        { name: "kernel_size", type: "int", min: 1, value: undefined },
        { name: "stride", type: "int", min: 1, value: undefined },
      ],
      tooltip:
        "평균 풀링 레이어로, 커널 영역 내의 값들을 평균하여 다운샘플링하는 기법입니다. MaxPool2d와 유사하지만, 큰 값 대신 평균값을 취해 다소 다른 특징을 추출할 수 있습니다. 이 방법은 특징의 대표성을 강조하는 데 유용합니다.",
    },
  ],
  Padding: [
    {
      name: "ReflectionPad2d",
      params: [{ name: "padding", type: "int", min: 0, value: undefined }],
      tooltip:
        "입력 이미지의 경계를 반사하여 패딩을 추가하는 레이어입니다. 이미지의 경계 부근에서도 정보 손실을 최소화하면서 크기를 조정할 수 있습니다.",
    },
    {
      name: "ReplicationPad2d",
      params: [{ name: "padding", type: "int", min: 0, value: undefined }],
      tooltip:
        "입력 이미지의 경계 값을 복제하여 패딩을 추가하는 레이어입니다. 특정 객체의 위치가 중요한 경우에 경계 값을 복제하여 정보 손실을 방지합니다.",
    },
    {
      name: "ZeroPad2d",
      params: [{ name: "padding", type: "int", min: 0, value: undefined }],
      tooltip:
        "입력 이미지의 경계를 0으로 채워 패딩을 추가하는 레이어입니다. 이 방법은 계산 효율이 높으며, 불필요한 정보를 추가하지 않습니다.",
    },
    {
      name: "ConstantPad2d",
      params: [
        { name: "padding", type: "int", min: 0, value: undefined },
        { name: "value", type: "float", value: undefined },
      ],
      tooltip:
        "입력 이미지의 경계를 특정 상수 값으로 채워 패딩을 추가하는 레이어입니다. 주로 이미지 데이터에 특정 기준 값을 추가하거나, 연산의 일관성을 유지할 때 유용합니다.",
    },
  ],
  Activation: [
    {
      name: "ReLU",
      params: [],
      tooltip:
        "ReLU(Rectified Linear Unit)는 비선형 활성화 함수로, 입력 값이 0보다 작으면 0으로, 0보다 크면 그대로 출력합니다. 연산이 간단해 CNN에서 기본 활성화 함수로 널리 사용됩니다.",
    },
    {
      name: "LeakyReLU",
      params: [
        { name: "negative_slope", type: "float", min: 0.0, value: undefined },
      ],
      tooltip:
        "LeakyReLU는 ReLU 함수의 변형으로, 입력 값이 0보다 작을 때도 작은 기울기를 남겨 죽은 뉴런 문제를 완화합니다. 이미지 데이터의 음수 특성까지 고려할 수 있습니다.",
    },
    {
      name: "ELU",
      params: [{ name: "alpha", type: "float", min: 0.0, value: undefined }],
      tooltip:
        "ELU(Exponential Linear Unit)는 LeakyReLU와 비슷하게 음수 입력에 대해 기울기를 남기면서도, 출력이 0에 수렴하도록 합니다. 이로 인해 ReLU보다 더 부드러운 경사면을 형성하여 안정적인 학습이 가능합니다.",
    },
    {
      name: "PReLU",
      params: [{ name: "init", type: "float", min: 0.0, value: undefined }],
      tooltip:
        "PReLU(Parametric ReLU)는 LeakyReLU와 유사하지만, 음수 입력에 대해 적용되는 기울기(negative slope)를 학습할 수 있습니다. 이는 모델이 데이터에 맞춰 최적의 기울기를 자동으로 조정할 수 있게 합니다.",
    },
    {
      name: "Sigmoid",
      params: [],
      tooltip:
        "Sigmoid 함수는 입력 값을 0과 1 사이의 값으로 변환하여 출력합니다. 주로 이진 분류 문제에서 마지막 레이어에 사용되며, 확률값을 제공하는 특성 덕분에 분류 기준으로 활용됩니다.",
    },
    {
      name: "Tanh",
      params: [],
      tooltip:
        "Tanh 함수는 입력을 -1에서 1 사이의 값으로 변환하며, 양수와 음수 출력을 제공할 수 있습니다. Sigmoid 함수보다 중앙에 가까운 출력을 제공하여 기울기 소실 문제가 다소 줄어듭니다.",
    },
    {
      name: "Softmax",
      params: [{ name: "dim", type: "int", min: 0, max: 2, value: undefined }],
      tooltip:
        "Softmax 함수는 입력 값을 정규화하여 각 클래스의 확률로 변환합니다. 출력의 모든 값이 0과 1 사이로 변환되며, 모든 출력의 합이 1이 됩니다. 다중 클래스 분류 문제에서 마지막 레이어로 많이 사용됩니다.",
    },
    {
      name: "LogSoftmax",
      params: [{ name: "dim", type: "int", min: 0, max: 2, value: undefined }],
      tooltip:
        "Softmax 함수의 로그 값을 출력하는 함수로, 분류 문제에서 손실 함수인 NLLLoss와 함께 사용됩니다. Softmax와 달리, 확률의 로그 값을 제공해 더 안정적인 학습을 가능하게 합니다.",
    },
    {
      name: "GELU",
      params: [],
      tooltip:
        "Gaussian Error Linear Unit(GELU)는 활성화 함수로, 입력 값을 Gaussian 분포에 따라 스무스하게 변환하여 학습을 안정화합니다. ReLU와 달리 입력 값의 변화에 따라 비선형적 반응을 제공합니다.",
    },
  ],
  Linear: [
    {
      name: "Linear",
      params: [
        { name: "in_features", type: "int", min: 1, value: undefined },
        { name: "out_features", type: "int", min: 1, value: undefined },
      ],
      tooltip:
        "완전 연결 레이어로, 입력의 모든 노드가 출력의 모든 노드와 연결되는 구조입니다. 주로 모델의 마지막 레이어로 사용되며, 전체적인 입력 정보의 요약 및 결합을 통해 최종 결과를 출력합니다. 다층 퍼셉트론(Multilayer Perceptron)에서 기본적으로 사용하는 레이어입니다.",
    },
  ],
};
