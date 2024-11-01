from typing import Union, Literal, List
from fastapi import FastAPI, status
# from pymilvus import connections, db, FieldSchema, MilvusClient
from classes import *
# from exception import *
from fastapi.responses import JSONResponse
from dotenv import load_dotenv
import json
import os
import torch

load_dotenv(verbose=True)
db_name = os.getenv("DB_NAME")
collection_name = os.getenv("COLLECTION_NAME")
milvus_host_name = os.getenv("MILVUS_HOST_NAME")
milvus_port = os.getenv("MILVUS_PORT")

app = FastAPI(root_path="/fast/v1/model/inference")

client = MilvusClient(
    uri="http://{}:{}".format(milvus_host_name, milvus_port),
    db_name=db_name
)

client.load_collection(
    collection_name=collection_name
)


# vectordb에서 한 레이어 조회
@app.get("/{model_version_id}/{layer_id}", response_model=Model_Read_Response)
def read_model(model_version_id: str, layer_id: str):

    print("{}_{} id 의 vector를 조회합니다.".format(model_version_id, layer_id))

    res = client.get(
        collection_name=collection_name,
        ids=["{}_{}".format(model_version_id, layer_id)]
    )
    
    print(res[0])
    
    res[0]["layers"] = deserialize_layers(res[0]["layers"])

    return res[0]

# vectordb에 한 레이어 추가
@app.post("/{model_version_id}/{layer_id}", response_model=Model_Insert_Response)
def insert_model(model_version_id: str, layer_id: str, req: Model_Insert_Request):

    res = client.insert(
        collection_name=collection_name,
        data=[
            {
                "model_version_layer_id" : model_version_id + "_" + layer_id,
                "model_version_id" : int(model_version_id),
                "test_accuracy" : req.test_accuracy,
                "layers" : json.dumps([layer.json() for layer in req.layers]),
                "cka_vec" : req.cka_vec
            }
        ]
    )

    print("{}_{} id로 vector를 insert 합니다. : {}".format(model_version_id, layer_id, res))

    res = dict(res)

    if res["upsert_count"] == 1 :
        return {"model_version_layer_id" : model_version_id + "_" + layer_id, "success": True}

    return {"model_version_layer_id" : model_version_id + "_" + layer_id, "success": False}

# vectordb에서 모델 삭제
@app.delete("/{model_version_id}")
def delete_model(model_version_id: str):
    
    print(f"{model_version_id} 의 model_version_id를 가진 레이어를 지웁니다.")

    res = client.delete(
        collection_name=collection_name,
        filter="model_version_id == {}".format(model_version_id)
    )

    res = dict(res)
    print("{}개의 layer가 삭제되었습니다.".format(res["delete_count"]))
    return JSONResponse(
        status_code=status.HTTP_200_OK,
        content = {
            "content": f"{res["delete_count"]}개의 layer가 삭제되었습니다."
        }
    )

# 유사 모델 검색
@app.get("/{model_version_id}/{layer_id}/search", response_model=Model_Search_Response)
async def search_model(model_version_id: str, layer_id: str):
    
    model_version_layer_id = "{}_{}".format(model_version_id, layer_id)

    model = client.get(
        collection_name=collection_name,
        ids=[model_version_layer_id]
    )

    print("layer를 찾았습니다. : {}".format(model))

    if (len(model) == 0):
        raise InvalidModelId(model_version_layer_id)
    
    model = model[0]

    print("{} id로 가장 유사한 레이어를 검색합니다.".format(model_version_layer_id))

    results = client.search(
        collection_name=collection_name,
        data=[model["cka_vec"]], 
        anns_field="cka_vec", 
        output_fields=["model_version_layer_id", "test_accuracy", "cka_vec", "layers"],
        search_params={"metric_type": "IP"}, 
        limit=1,
        filter="model_version_layer_id != '{}'".format(model_version_layer_id)
        # 성능이 더 좋은 모델만 찾아주려면, 아무 것도 찾지 못했을 수 있음
        # filter="test_accuracy > {}".format(model[0]["test_accuracy"])
    )

    print(results)

    if(len(results[0]) == 0) :
        raise LayerNotFound()

    results = dict(results[0][0])

    id_parse = results["id"].split("_")
    searched_model_version_id = id_parse[0]
    searched_layer_id = id_parse[1]
    searched_test_accuracy = results["entity"]["test_accuracy"]
    
    target = model["layers"]
    target_test_accuracy = model["test_accuracy"]
    searched = results["entity"]["layers"]

    gpt_description = await get_gpt_answer(target, searched, layer_id, searched_layer_id, target_test_accuracy, searched_test_accuracy)
    
    searched = deserialize_layers(searched)

    return {"model_version_id": searched_model_version_id, "layer_id": searched_layer_id, "gpt_description": gpt_description, "test_accuracy": searched_test_accuracy, "layers": searched}

@app.exception_handler(InvalidModelId)
def invalid_model_id_exception_handler(req, exc: InvalidModelId):
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={
            "content": "invalid model id 입니다.",
            "model_id" : exc.model_version_layer_id
        }
    )

@app.exception_handler(LayerNotFound)
def layer_not_found_exception_handler(req, exc: LayerNotFound):
    return JSONResponse(
        status_code=status.HTTP_404_NOT_FOUND,
        content = {
            "content": "레이어를 찾지 못했습니다."
        }
    )