from dotenv import load_dotenv
from pymilvus import connections, db, FieldSchema, MilvusClient
import os
import numpy as np
import httpx
from load_minio import load_dataset_from_minio
from collections import defaultdict
import torch

load_dotenv(verbose=True)

fast_match_host_name = os.getenv("FAST_MATCH_HOST_NAME")
fast_match_port = os.getenv("FAST_MATCH_PORT")

async def save_cka_to_milvus(model, dataset, model_version_id, conv_idx, test_accuracy, layers):
    cka_dataset = load_dataset_from_minio(dataset, "cka")
# Milvus CKA 저장
    cka_matrix = defaultdict(list)
    with torch.no_grad():
        for index, (input, label) in enumerate(cka_dataset):

            x = input
            
            for i in range(0, len(model)):
                x = model[i](x)
                if i in conv_idx:
                    cka_matrix[i].append(torch.flatten(x).cpu().numpy())
                    

    for i in cka_matrix.keys():
        mat = np.array(cka_matrix[i])
        cka = (mat @ mat.T).flatten()
        cka_vec = cka / np.linalg.norm(cka)
        async with httpx.AsyncClient() as client:
            res = await client.post(f"http://{fast_match_host_name}:{fast_match_port}/fast/v1/model/match/{model_version_id}/{i}",
                        json={
                            "test_accuracy": test_accuracy,
                            "layers" : layers,
                            "cka_vec": cka_vec.tolist()
                        })
            print(res)