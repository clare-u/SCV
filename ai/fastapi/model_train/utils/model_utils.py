import logging
from typing import Union

from fastapi import HTTPException

logger = logging.getLogger(__name__)


def generate_model_name(model_id: Union[int, str], version_id: Union[int, str]) -> str:
    """모델ID랑 버전 ID로 고유한 모델 이름 생성"""
    try:
        model_id = int(model_id)
        version_id = int(version_id)

        if model_id < 0 or version_id < 0:
            raise ValueError("모델 ID와 버전 ID는 0 이상이어야 합니다.")

        model_name = f"model_{model_id}_v{version_id}"
        logger.debug(f"Generated model name: {model_name}")

        return model_name

    except ValueError as e:
        error_msg = f"잘못된 입력값: {str(e)}"
        logger.error(error_msg)
        raise HTTPException(status_code=400, detail=error_msg)
    except Exception as e:
        error_msg = f"모델 이름 생성 중 오류 발생: {str(e)}"
        logger.error(error_msg)
        raise HTTPException(status_code=500, detail=error_msg)
