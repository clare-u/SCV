from typing import Union, Literal, List
from pydantic import BaseModel
from fastapi import FastAPI
from pymilvus import connections, db

app = FastAPI(root_path="/fast/v1/model/match")

# conn = connections.connect(host="localhost", port=19530)
database = db.create_database("scv_database")
db.using_database("scv_database")


class Model_Search_Response(BaseModel):
    model_version_id: int
    layer_id: int
    gptDescription: str
    testAccuracy: float



@app.get("/{model_version_id}/{layer_id}", response_model=Model_Search_Response)
def search_model():


    
    return JSON_Topic_Out(data=topics_list)



