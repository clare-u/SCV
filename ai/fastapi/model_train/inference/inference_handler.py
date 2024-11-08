import logging
import os.path
from datetime import time
from pathlib import Path
from typing import Optional, Dict, Any

import torch
from minio import Minio

from inference.exceptions import ModelLoadException, InferenceException
from save_minio import minio_host_name, minio_api_port, minio_user_name, minio_user_password, \
    minio_model_bucket

logger = logging.getLogger(__name__)


class ModelInferenceHandler:
    """학습된 모델을 사용해 사용자 이미지 추론 클래스"""

    def __init__(
            self,
            model_path: str,
            device: Optional[str] = None,
            cache_dir: Optional[str] = None,
    ):

        self.device = device or ("cuda" if torch.cuda.is_available() else "cpu")
        self.cache_dir = cache_dir or ".model_cache"
        Path(self.cache_dir).mkdir(parents=True, exist_ok=True)

        self.model = self._load_model(model_path)
        self.model.to(self.device)
        self.model.eval()

        # 데이터셋 정보 확인
        if not hasattr(self.model, "dataset_info"):
            raise ModelLoadException("모델에 데이터셋 정보가 없습니다.")

        self.num_classes = self.model.dataset_info["num_classes"]

    def _get_cached_path(self, model_path: str) -> str:
        """캐시된 모델 경로 반환"""
        return os.path.join(self.cache_dir, Path(model_path).name)

    def _load_model(self, model_path: str) -> torch.nn.Module:
        """MinIO에서 모델 로드 (캐시 지원함)"""
        try:
            cached_path = self._get_cached_path(model_path)

            # 캐시확인
            if os.path.exists(cached_path):
                logger.info(f"캐시된 모델 사용: {cached_path}")
                return torch.load(cached_path, map_location=self.device)

            # MinIO에서 다운받기
            client = Minio(
                endpoint=f"{minio_host_name}:{minio_api_port}",
                access_key=minio_user_name,
                secret_key=minio_user_password,
                secure=False
            )

            client.fget_object(minio_model_bucket, model_path, cached_path)

            # 모델 로드
            model = torch.load(cached_path, map_location=self.device)
            logger.info(f"모델 로드 완료: {model_path}")

            return model

        except Exception as e:
            raise ModelLoadException(f"모델 로드 중 오류 발생: {str(e)}")

    def predict(self, input_tensor: torch.Tensor) -> Dict[str, Any]:
        """입력 텐서에 대한 추론"""
        try:
            input_tensor = input_tensor.to(self.device)

            with torch.no_grad():
                output = self.model(input_tensor)
                probabilities = torch.nn.functional.softmax(output, dim=1)

                # 배치처리
                batch_size = input_tensor.size(0)
                if batch_size == 1:
                    predicted_class = torch.argmax(probabilities, dim=1).item()
                    confidence = probabilities[0][predicted_class].item()

                    return {
                        "predicted_class": predicted_class,
                        "confidence": confidence,
                        "probability": probabilities[0].tolist()
                    }
                else:
                    predictions = []
                    for i in range(batch_size):
                        predicted_class = torch.argmax(probabilities[i]).item()
                        confidence = probabilities[i][predicted_class].item()
                        predictions.append({
                            "predicted_class": predicted_class,
                            "confidence": confidence,
                            "probability": probabilities[i].tolist()
                        })
                    return {"batch_predictions": predictions}

        except Exception as e:
            raise InferenceException(f"추론 중 오류 발생: {str(e)}")

    def cleanup_cache(self, max_age: int = 7 * 24 * 3600) -> None:
        """캐시 파일 정리"""
        try:
            current_time = time.time()
            for cached_file in Path(self.cache_dir).glob("*.pth"):
                if current_time - cached_file.stat().st_mtime > max_age:
                    cached_file.unlink()
                    logger.info(f"캐시 파일 삭제: {cached_file}")
        except Exception as e:
            logger.error(f"캐시 처리 중 오류 발생 {str(e)}")
