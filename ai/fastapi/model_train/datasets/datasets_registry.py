from dataclasses import dataclass
from typing import Tuple, Optional, Dict
import yaml
import logging


# 로거 설정
def setup_logger():
    logger = logging.getLogger("DatasetRegistry")
    if not logger.handlers:  # 핸들러가 없을 때만 추가
        logger.setLevel(logging.INFO)

        # 콘솔 핸들러
        console_handler = logging.StreamHandler()
        console_handler.setLevel(logging.INFO)

        # 포맷터
        formatter = logging.Formatter(
            '%(asctime)s - %(name)s - %(levelname)s - %(message)s',
            datefmt='%Y-%m-%d %H:%M:%S'
        )
        console_handler.setFormatter(formatter)
        logger.addHandler(console_handler)

        # 파일 핸들러 (선택적)
        file_handler = logging.FileHandler('dataset_registry.log')
        file_handler.setLevel(logging.INFO)
        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)

    return logger


@dataclass
class DatasetInfo:
    name: str
    input_shape: Tuple[int, int, int]  # channels, height, width
    num_classes: int
    mean: Tuple[float, ...]
    std: Tuple[float, ...]
    augmentation: bool = False
    resize_dims: Optional[Tuple[int, int]] = None

    def to_dict(self) -> Dict:
        return {
            "name": self.name,
            "input_shape": self.input_shape,
            "num_classes": self.num_classes,
            "mean": self.mean,
            "std": self.std,
            "augmentation": self.augmentation,
            "resize_dims": self.resize_dims
        }

    @classmethod
    def from_dict(cls, data: Dict) -> 'DatasetInfo':
         return cls(
            name=data["name"],
            input_shape=tuple(data["input_shape"]),
            num_classes=data["num_classes"],
            mean=tuple(data["mean"]),
            std=tuple(data["std"]),
            augmentation=data.get("augmentation", False),
            resize_dims=tuple(data["resize_dims"]) if data.get("resize_dims") else None
        )

class DatasetRegistry:
    _registry: Dict[str, DatasetInfo] = {}
    logger = setup_logger()

    @classmethod
    def register_dataset(cls, name: str, info: dict):
        try:
            cls._registry[name] = DatasetInfo(name=name, **info)
            cls.logger.info(f"데이터셋 등록 완료: {name}")
        except Exception as e:
            cls.logger.error(f"데이터셋 {name} 등록 중 오류 발생: {str(e)}")
            raise

    @classmethod
    def get_dataset_info(cls, name: str) -> DatasetInfo:
        if name not in cls._registry:
            cls.logger.error(f"알 수 없는 데이터셋: {name}")
            raise ValueError(f"알 수 없는 데이터셋: {name}")
        return cls._registry[name]

    @classmethod
    def load_dataset_configs(cls, config_path: str):
        """YAML 설정 파일에서 데이터셋 정보 로드"""
        try:
            with open(config_path, 'r', encoding='utf-8') as f:
                configs = yaml.safe_load(f)

            success_count = 0
            for dataset_name, config in configs.get('datasets', {}).items():
                try:
                    # 필수 필드만 검증
                    required_fields = ['input_shape', 'num_classes', 'mean', 'std']
                    missing_fields = [field for field in required_fields if field not in config]

                    if missing_fields:
                        cls.logger.warning(f"{dataset_name} 데이터셋에 필수 필드 누락: {missing_fields}")
                        continue

                    cls.register_dataset(dataset_name, config)
                    success_count += 1

                except Exception as e:
                    cls.logger.error(f"데이터셋 {dataset_name} 처리 중 오류 발생: {str(e)}")
                    continue

            cls.logger.info(f"총 {success_count}개의 데이터셋 구성을 성공적으로 로드했습니다.")

        except Exception as e:
            cls.logger.error(f"데이터셋 구성을 로드하는 중 오류 발생: {str(e)}")
            raise

    @classmethod
    def get_transform_params(cls, name: str) -> tuple:
        info = cls.get_dataset_info(name)
        return (info.input_shape, info.mean, info.std,
                info.augmentation, info.resize_dims)