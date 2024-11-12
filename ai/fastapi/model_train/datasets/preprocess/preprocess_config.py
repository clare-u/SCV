import logging
from typing import Dict, Any

from neural_network_builder.config.project_config import project_config  # 기존 ProjectConfig 임포트

logger = logging.getLogger(__name__)


class PreprocessConfig:
    """데이터셋별 전처리 파라미터 관리"""

    def __init__(self):
        self.params = self._load_config()
        self.default_params = self.params.get('default', {})

        logger.info(f"전처리 설정 파일 로드됨")
        logger.info(f"사용 가능한 데이터셋: {list(self.params.keys())}")

    def _load_config(self) -> Dict[str, Any]:
        """ProjectConfig를 통해 YAML 설정 파일 로드"""
        try:
            return project_config.load_yaml_config('preprocess')
        except Exception as e:
            logger.error(f"전처리 파라미터 로드 실패: {str(e)}")
            raise ValueError(f"전처리 파라미터 로드 실패: {str(e)}")

    def get_params(self, dataset_name: str) -> Dict[str, Any]:
        """데이터셋별 파라미터 반환"""
        params = self.params.get(dataset_name, self.default_params)
        logger.debug(f"데이터셋 {dataset_name}의 전처리 파라미터: {params}")
        return params

    def list_datasets(self) -> list:
        """사용 가능한 데이터셋 목록 반환"""
        return [k for k in self.params.keys() if k != 'default']

    def validate_params(self, dataset_name: str) -> bool:
        """데이터셋의 필수 파라미터 존재 여부 검증"""
        if dataset_name not in self.params:
            return False

        required_params = {'blur_kernel', 'padding_ratio'}
        current_params = set(self.params[dataset_name].keys())

        return required_params.issubset(current_params)
