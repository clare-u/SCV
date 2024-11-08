import logging
import os
from tempfile import NamedTemporaryFile
from typing import Dict

from dotenv import load_dotenv
from fastapi import APIRouter, File, UploadFile, HTTPException, Path
from minio import Minio

from model_train.inference.exceptions import InvalidInputException, ModelLoadException, InferenceException, \
    DataPreprocessException
from model_train.inference.image_processor import ImageProcessor
from model_train.inference.inference_handler import ModelInferenceHandler
from model_train.utils.model_utils import generate_model_name

load_dotenv()

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/fast/v1")

# MinIO 클라이언트 초기화
minio_client = Minio(
    endpoint=f"{os.getenv('MINIO_HOST_NAME')}:{os.getenv('MINIO_API_PORT')}",
    access_key=os.getenv('MINIO_USER_NAME'),
    secret_key=os.getenv('MINIO_USER_PASSWORD'),
    secure=False
)

CLASS_LABELS = {
    "MNIST": {i: str(i) for i in range(10)},

    "FASHION_MNIST": {
        0: "T-shirt/top", 1: "Trouser", 2: "Pullover",
        3: "Dress", 4: "Coat", 5: "Sandal",
        6: "Shirt", 7: "Sneaker", 8: "Bag", 9: "Ankle boot"
    },

    "CIFAR10": {
        0: "Airplane", 1: "Automobile", 2: "Bird",
        3: "Cat", 4: "Deer", 5: "Dog",
        6: "Frog", 7: "Horse", 8: "Ship", 9: "Truck"
    },

    "SVHN": {i: str(i) for i in range(10)},  # Street View House Numbers (0-9)

    "EMNIST": {  # Extended MNIST (알파벳 A-Z)
        0: "A", 1: "B", 2: "C", 3: "D", 4: "E", 5: "F",
        6: "G", 7: "H", 8: "I", 9: "J", 10: "K", 11: "L",
        12: "M", 13: "N", 14: "O", 15: "P", 16: "Q", 17: "R",
        18: "S", 19: "T", 20: "U", 21: "V", 22: "W", 23: "X",
        24: "Y", 25: "Z"
    }
}


def get_class_label(dataset_name: str, class_idx: int) -> str:
    """클래스 인덱스를 레이블로 변환"""
    dataset_labels = CLASS_LABELS.get(dataset_name, {})
    return dataset_labels.get(class_idx, str(class_idx))


@router.post("/models/{modelId}/versions/{versionId}/my-data")
async def test_with_my_data(
        modelId: int = Path(..., title="모델 Id", description="모델 ID"),
        versionId: int = Path(..., title="버전 ID", description="버전 ID"),
        file: UploadFile = File(..., description="사용자 이미지 파일 (PNG, JPG, JPEG)")
) -> Dict:
    """사용자 데이터로 모델 테스트"""
    temp_paths = None
    inference_handler = None

    try:
        # 지원하는 이미지 형식
        content_type = file.content_type
        if not content_type or not content_type.startswith('image/'):
            raise InvalidInputException("이미지 파일만 업로드 가능합니다 (PNG, JPG, JPEG)")

        # 모델 경로 생성
        model_name = generate_model_name(modelId, versionId)
        model_path = f"{model_name}.pth"

        # InferenceHandler 인스턴스
        inference_handler = ModelInferenceHandler(
            model_path=model_path,
            device=None,
            cache_dir=".model_cache",
        )

        with NamedTemporaryFile(delete=False, suffix=os.path.splitext(file.filename)[1]) as temp_file:
            contents = await file.read()
            temp_file.write(contents)
            temp_path = temp_file.name

        # 모델 로드
        # model = inference_handler._load_model(model_path, model_path)

        # 이미지 프로세서
        image_processor = ImageProcessor(inference_handler.model.dataset_info)

        # 이미지 처리
        input_tensor = image_processor.process_image(temp_path)

        # 입력 데이터 검증
        image_processor.validate_image(
            image_path=temp_path,
        )

        # 이미지 전처리
        input_tensor = image_processor.process_image(temp_path)

        # 정답 추론
        result = inference_handler.predict(input_tensor)

        dataset_name = inference_handler.model.dataset_info.get("name")
        if dataset_name in CLASS_LABELS:
            result["predicted_label"] = CLASS_LABELS[dataset_name].get(
                result["predicted_class"],
                str(result["predicted_class"])
            )

        result_my_data = {
            "status": "success",
            "modelId": modelId,
            "versionId": versionId,
            "filename": file.filename,
            "result": result,
            "model_info": {
                "dataset": dataset_name,
                "input_shape": inference_handler.model.dataset_info.get("input_shape"),
            }
        }

        # return 값을 로그로 출력
        logger.info(f"\nPredicted Label: {result.get('predicted_label', '')}\n")

        return result_my_data



    except InvalidInputException as e:
        logger.error(f"입력 검증 실패: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))

    except ModelLoadException as e:
        logger.error(f"모델 로드 실패: {str(e)}")
        raise HTTPException(status_code=404, detail=str(e))

    except DataPreprocessException as e:
        logger.error(f"데이터 전처리 실패: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))

    except InferenceException as e:
        logger.error(f"추론 실패: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

    except Exception as e:
        logger.error(f"예상치 못한 오류 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


    finally:
        await file.close()

        if temp_path and os.path.exists(temp_path):
            try:
                os.remove(temp_path)
            except Exception as e:
                logger.error(f"임시 파일 삭제 중 오류 발생: {str(e)}")

        if inference_handler:
            del inference_handler
