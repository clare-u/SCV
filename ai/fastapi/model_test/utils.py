from classes import Train_Info, feature_activation, activation_maximization

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

def get_example_image() -> str:
    return [["string", "string"],["string", "string"]]

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


