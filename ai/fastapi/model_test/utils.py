from collections import defaultdict
from classes import Train_Info, feature_activation, activation_maximization
import json
import torch

dataset_labels = {
    "mnist": [0,1,2,3,4,5,6,7,8,9]
}

def get_code() -> str:
    return "string"

def get_test_accuracy() -> float:
    return 0.0

def get_test_loss() -> float:
    return 0.0

def get_train_info() -> Train_Info:
    return {
        "loss" : [0.0],
        "accuracy" : [0.0]
        }

def get_confusion_matrix() -> str:
    return "string"

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
        # print(output)
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

def get_feature_activation() -> list[feature_activation]:
    return [
        {
            "origin": "string",
            "visualize": "string"
        }
    ]

def get_activation_maximization() -> list[activation_maximization]:
    return [
        {
            "label": "string",
            "image": "string"
        }
    ]


