import os
from pathlib import Path
from typing import Dict, Any
import yaml
from ..exceptions.custom_exceptions import ConfigurationError


class ProjectConfig:
    """프로젝트 전체 설정을 관리하는 클래스"""

    def __init__(self):
        self.project_root = self._get_project_root()
        self._load_config()

    def _get_project_root(self) -> Path:
        """프로젝트 루트 디렉토리 경로 반환"""
        current_file = Path(__file__).resolve()
        return current_file.parent.parent.parent.parent  # fastapi 디렉토리

    def _load_config(self) -> None:
        """설정 파일 로드 및 환경 변수 처리"""
        try:
            # 기본 설정 파일 경로
            self.config_paths = {
                'datasets': self.project_root / 'model_train' / 'datasets' / 'configs' / 'datasets.yaml'
            }

            # 환경 변수로 설정 파일 경로 오버라이드 가능
            if os.getenv('DATASETS_CONFIG_PATH'):
                self.config_paths['datasets'] = Path(os.getenv('DATASETS_CONFIG_PATH'))

        except Exception as e:
            raise ConfigurationError(f"설정 로드 중 오류 발생: {str(e)}")

    def get_config_path(self, config_name: str) -> Path:
        """설정 파일 경로 반환"""
        if config_name not in self.config_paths:
            raise ConfigurationError(f"알 수 없는 설정 파일: {config_name}")
        return self.config_paths[config_name]

    def load_yaml_config(self, config_name: str) -> Dict[str, Any]:
        """YAML 설정 파일 로드"""
        try:
            config_path = self.get_config_path(config_name)
            with open(config_path, 'r', encoding='utf-8') as f:
                return yaml.safe_load(f)
        except Exception as e:
            raise ConfigurationError(f"YAML 설정 파일 로드 실패 ({config_name}): {str(e)}")


# 싱글톤 인스턴스 생성
project_config = ProjectConfig()