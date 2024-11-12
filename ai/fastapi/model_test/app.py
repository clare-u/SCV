from typing import Union, Literal, List
from fastapi import FastAPI, status
from classes import *
from utils import *
from exception import *
from fastapi.responses import JSONResponse
from dotenv import load_dotenv
from load_minio import load_model_from_minio, load_dataset_from_minio
from model_layer_class import deserialize_layers, serialize_layers
import os
import torch
from save_milvus import save_cka_to_milvus

load_dotenv(verbose=True)

app = FastAPI(root_path="/fast/v1/model/test")

# 모델이 확정되어 결과 분석, CKA 저장 하는 함수
@app.get("/analyze/{model_id}/{version_id}/{dataset}", response_model=Model_Analyze_Response)
async def analyze_model(model_id: str, version_id:str, dataset: Literal["mnist", "fashion_mnist", "cifar10", "svhn", "emnist"]):

    # model 가져오기
    model = load_model_from_minio(f"model_{model_id}_v{version_id}")
    layers = model.layers
    # 데이터 셋 가져오기
    test_dataset = load_dataset_from_minio(dataset, "test")


    # convolution layer의 index 보관
    conv_idx = []

    for i in range(0, len(model)):
        if isinstance(model[i], torch.nn.Conv2d):
            conv_idx.append(i)
    print(f"convolution layer의 index: {conv_idx}")

    # feature activation map 을 위한 변수 선언
    norm = [0, 0, 0]
    maximization_input = [None, None, None]
    activation_map = [None, None, None]

    # confusion matrix 를 위한 변수 선언

    true_label = []
    predicted_label = []

    outputs = []
    # 테스트
    model.eval()
    with torch.no_grad():
        for index, (input, label) in enumerate(test_dataset):

            x = input

            # confusion matrix를 위한 true label 저장
            true_label.extend(label.numpy())

            for i in range(0, len(model)):
                x = model[i](x)
                if i == conv_idx[-1]:
                    for j in range(0,3):
                        # print(torch.norm(x[0][j]))
                        curr_norm = torch.norm(x[0][j])
                        if curr_norm > norm[j] :
                            norm[j] = curr_norm
                            activation_map[j] = x[0][j]
                            maximization_input[j] = input

            # confusion matrix 를 위한 예측 레이블 저장
            predicted_label.extend(torch.max(x, 1)[1].numpy())

            outputs.append({
                "input" : input,
                "label": label,
                "output": x
            })

    code = get_code() # 현재
    test_accuracy = get_test_accuracy(model) # 나
    test_loss = get_test_loss() # 현재
    train_info = get_train_info() # 현재
    confusion_matrix = get_confusion_matrix(true_label, predicted_label) # 나
    example_image = get_example_image(outputs, dataset) # 나
    total_params = get_total_params() # 현재
    params = get_params() # 현재
    feature_activation = get_feature_activation(maximization_input, activation_map) # 나
    activation_maximization = get_activation_maximization(model, dataset) # 나

    _ = await save_cka_to_milvus(model, dataset, f"model_{model_id}_v{version_id}", conv_idx, test_accuracy, layers)

    return {
        "model_version_id": f"model_{model_id}_v{version_id}",
        "code": code,
        "dataset": dataset,
        "test_accuracy":test_accuracy,
        "test_loss": test_loss,
        "train_info":train_info,
        "confusion_matrix": confusion_matrix,
        "example_image": example_image,
        "total_params": total_params,
        "params": params,
        "feature_activation" : feature_activation,
        "activation_maximization": activation_maximization
    }


@app.exception_handler(ModelNotFound)
def invalid_model_id_exception_handler(req, exc: ModelNotFound):
    return JSONResponse(
        status_code=status.HTTP_404_NOT_FOUND,
        content={
            "content": "model을 찾을 수 없습니다.",
            "model_version_id" : exc.model_version_id
        }
    )