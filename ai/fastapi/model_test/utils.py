from collections import defaultdict
from classes import Train_Info, feature_activation, activation_maximization
import json
import torch
from torch.optim import Adam
from sklearn.metrics import confusion_matrix

dataset_labels = {
    "mnist": [0,1,2,3,4,5,6,7,8,9],
    "fashion_mnist": [0,1,2,3,4,5,6,7,8,9],
    "cifar10": [0,1,2,3,4,5,6,7,8,9],
    "svhn": [0,1,2,3,4,5,6,7,8,9],
    "emnist": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25]
}

dataset_width = {
    "mnist": 28,
    "fashion_mnist": 28,
    "cifar10": 32,
    "svhn": 32,
    "emnist": 28
}

dataset_channels = {
    "mnist": 1,
    "fashion_mnist": 1,
    "cifar10": 3,
    "svhn": 3,
    "emnist": 1
}

def get_code() -> str:
    return "string"

def get_test_accuracy(model) -> float:
    return model.test_accuracy

def get_test_loss() -> float:
    return 0.0

def get_train_info() -> Train_Info:
    return {
        "loss" : [0.0],
        "accuracy" : [0.0]
        }

def get_confusion_matrix(true, pred) -> str:
    conf_matrix = confusion_matrix(true, pred)

    return json.dumps(conf_matrix.tolist())

def get_example_image(outputs, dataset) -> str:

    data_labels = dataset_labels[dataset]

    example_images = {}

    for actual in data_labels:
        for pred in data_labels:
            example_images[(actual,pred)] = {
                "conf" : 0.0,
                "image" : "null"
            }

    for i in range(0, len(outputs)):
        input = outputs[i]["input"]
        label = outputs[i]["label"]
        output = outputs[i]["output"]
        label = label.item()

        conf, preds = torch.max(torch.softmax(output, dim=1),dim=1)
        conf = conf.item()
        preds = preds.item()
        curr_conf = example_images[(label, preds)]["conf"]
        if conf > curr_conf:
            example_images[(label, preds)] = {
                "conf" : conf,
                "image" : input.tolist()
            }
    return json.dumps({str(key): value for key, value in example_images.items()})

def get_total_params() -> int:
    return 0

def get_params() -> list[int]:
    return [0, 1]

# 특정 activation map의 maximum activating patch
def get_feature_activation(origin, activation) -> list[feature_activation]:

    resp = [{"origin": json.dumps(torch.squeeze(ori).tolist()), "visualize": json.dumps(activ.tolist())} for ori, activ in zip(origin, activation)]

    return resp

# 가장 라벨 스러운 이미지를 출력
def get_activation_maximization(model, dataset) -> list[activation_maximization]:

    resp = []
    for label in dataset_labels["mnist"]:
        resp.append({
            "label": str(label),
            "image": json.dumps(maximize_class_image(model, label, dataset))
        })
    return resp



def maximize_class_image(model, target_class, dataset, num_steps=100, lr=0.1):
    # 랜덤한 노이즈 이미지 생성 (MNIST: 1x28x28)
    optimized_image = torch.randn((1, dataset_channels[dataset], dataset_width[dataset], dataset_width[dataset]), requires_grad=True)

    # Adam 옵티마이저 설정
    optimizer = Adam([optimized_image], lr=lr)

    for step in range(num_steps):
        optimizer.zero_grad()

        # 모델의 예측 값 계산
        output = model(optimized_image)
        
        # target class에 대한 점수를 최대화하는 방향으로 손실 계산
        loss = -output[0, target_class]  # 음수를 취하여 최대화 방향으로 최적화

        # 역전파 및 최적화
        loss.backward()
        optimizer.step()

        # 이미지 값 범위 조정 (-1, 1 사이로 클리핑)
        with torch.no_grad():
            optimized_image.clamp_(-1, 1)

    return optimized_image.detach().cpu().squeeze().tolist()
