import torch
import io

from minio import Minio
from dotenv import load_dotenv
import os

from exception import ModelNotFound, DataSetNotFound

from typing import Literal

import pickle

load_dotenv(verbose=True)
minio_user_name=os.getenv("MINIO_USER_NAME")
minio_user_password=os.getenv("MINIO_USER_PASSWORD")
minio_host_name=os.getenv("MINIO_HOST_NAME")
minio_model_bucket=os.getenv("MINIO_MODEL_BUCKET")
minio_dataset_bucket=os.getenv("MINIO_DATASET_BUCKET")

client = Minio("{}:9002".format(minio_host_name),
        access_key=minio_user_name,
        secret_key=minio_user_password,
        # SSL 설정 해제
        secure=False
    )

def load_model_from_minio(model_version_id : str):
    try:
        obj = client.get_object(
            bucket_name=minio_model_bucket,
            object_name=f"{model_version_id}.pth",
        )
        # 바이트 데이터를 메모리 파일로 변환
        model_bytes = io.BytesIO(obj.read())
        # PyTorch 모델 로드
        model = torch.load(model_bytes)
        # model = torch.jit.load(model_bytes, map_location='cpu')
    except:
        raise ModelNotFound(model_version_id)
    finally:
        obj.close()
        obj.release_conn()
    print(model)
    return model

def load_dataset_from_minio(dataset : Literal["mnist, fashion_mnist, svhn, cifar10, emnist"], kind: Literal["train", "test", "cka"]):
    try:
        obj = client.get_object(
            bucket_name=minio_dataset_bucket,
            object_name=f"{dataset}_{kind}",
        )
                # 바이트 데이터를 메모리 파일로 변환
        dataset_bytes = io.BytesIO(obj.read())
        # PyTorch 모델 로드
        dataset = pickle.load(dataset_bytes)
    except:
        raise DataSetNotFound(f"{dataset}_{kind}")

    return dataset