import torch
import io

from minio import Minio
from dotenv import load_dotenv
import os
from pathlib import Path


# 환경 변수 로드
def load_env_vars():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    env_path = os.path.join(current_dir, '.env')
    load_dotenv(dotenv_path=env_path)

    # 환경 변수 가져오기
    return {
        "minio_user_name": os.getenv("MINIO_USER_NAME", "minioadmin"),
        "minio_user_password": os.getenv("MINIO_USER_PASSWORD", "minioadmin"),
        "minio_host_name": os.getenv("MINIO_HOST_NAME", "localhost"),
        "minio_api_port": os.getenv("MINIO_API_PORT", "9000"),
        "minio_model_bucket": os.getenv("MINIO_MODEL_BUCKET", "model-bucket")
    }


env_vars = load_env_vars()
minio_user_name = env_vars["minio_user_name"]
minio_user_password = env_vars["minio_user_password"]
minio_host_name = env_vars["minio_host_name"]
minio_api_port = env_vars["minio_api_port"]
minio_model_bucket = env_vars["minio_model_bucket"]

# MinIO 클라이언트 초기화
client = Minio(
    endpoint=f"{minio_host_name}:{minio_api_port}",  # endpoint 매개변수 명시
    access_key=minio_user_name,
    secret_key=minio_user_password,
    secure=False  # 로컬 개발환경이므로 False
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
        # torch.jit.script(model, buffer)
        torch.save(model, buffer)
        buffer.seek(0)

        # MinIO에 업로드
        client.put_object(
            minio_model_bucket,
            f"{file_name}.pth",
            buffer,
            length=buffer.getbuffer().nbytes
        )
        print(f"모델 저장 성공: {file_name}.pth")
    except Exception as e:
        print(f"모델 저장 오류: {e}")
        raise

__all__ = [
    'save_model_to_minio',
    'minio_host_name',
    'minio_user_name',
    'minio_user_password',
    'minio_model_bucket',
    'minio_api_port'
]