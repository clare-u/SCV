from dataclasses import dataclass
from typing import Tuple, Optional
import yaml

@dataclass
class DatasetInfo:
    name: str
    input_shape: Tuple[int, int, int]  # channels, height, width
    num_classes: int
    mean: Tuple[float, ...]
    std: Tuple[float, ...]
    augmentation: bool = False
    resize_dims: Optional[Tuple[int, int]] = None

class DatasetRegistry:
    _registry = {}

    @classmethod
    def register_dataset(cls, name: str, info: dict):
        cls._registry[name] = DatasetInfo(name=name, **info)

    @classmethod
    def get_dataset_info(cls, name: str) -> DatasetInfo:
        if name not in cls._registry:
            raise ValueError(f"Unknown dataset: {name}")
        return cls._registry[name]

    @classmethod
    def load_dataset_configs(cls, config_path: str):
        """Load dataset configurations from YAML file"""
        with open(config_path) as f:
            configs = yaml.safe_load(f)
        for name, config in configs.items():
            cls.register_dataset(name, config)