import torch
import io

from minio import Minio
from dotenv import load_dotenv
import os
from pathlib import Path

# .env 파일 위치 확인
current_dir = os.path.dirname(os.path.abspath(__file__))
env_path = os.path.join(current_dir, '.env')
load_dotenv(dotenv_path=env_path)
load_dotenv(verbose=True)

minio_user_name=os.getenv("MINIO_USER_NAME")
minio_user_password=os.getenv("MINIO_USER_PASSWORD")
minio_host_name=os.getenv("MINIO_HOST_NAME")
minio_model_bucket=os.getenv("MINIO_MODEL_BUCKET")

client = Minio("{}:9002".format(minio_host_name),
        access_key=minio_user_name,
        secret_key=minio_user_password,
        # SSL 설정 해제
        secure=False
    )

# 버킷 존재 여부 확인 및 생성
try:
    if not client.bucket_exists(minio_model_bucket):
        client.make_bucket(minio_model_bucket)
        print(f"Bucket '{minio_model_bucket}' created successfully")
except Exception as e:
    print(f"Error with MinIO setup: {e}")

def save_model_to_minio(model, file_name: str):
    try:
        # 모델 저장
        buffer = io.BytesIO()
        # torch.jit.save(model, buffer)
        torch.save(model, buffer)
        buffer.seek(0)

        # MinIO에 업로드
        client.put_object(
            minio_model_bucket,
            f"{file_name}.pth",
            buffer,
            length=buffer.getbuffer().nbytes
        )
        print(f"Model saved successfully as {file_name}.pth")
    except Exception as e:
        print(f"Error saving model: {e}")
        raise