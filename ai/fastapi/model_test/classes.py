from pydantic import BaseModel, Json
from typing import Union, Literal, List, Any
from model_layer_class import Layer

class Train_Info(BaseModel):
    loss: List[float]
    accuracy: List[float]

class feature_activation(BaseModel):
    origin: str
    visualize: str

class activation_maximization(BaseModel):
    label: str
    image: str

# class Model_Analyze_Request(BaseModel):
#     # layers: str

class Model_Analyze_Response(BaseModel):
    model_version_id: str
    code: str
    dataset: str
    test_accuracy: float
    test_loss: float
    train_info: Train_Info
    confusion_matrix: str
    example_image: str
    total_params: int
    params: List[int]
    feature_activation : List[feature_activation]
    activation_maximization: List[activation_maximization]

