from typing import Dict

from dataset_preprocessors import *


class PreprocessorFactory:
    """전처리기 팩토리 클래스"""
    _preprocessors = {
        "MNIST": MNISTPreprocessor,
        "FASHION_MNIST": FashionMNISTPreprocessor,
        "CIFAR10": CIFAR10Preprocessor,
        "SVHN": SVHNPreprocessor,
        "EMNIST": EMNISTPreprocessor,
    }

    @classmethod
    def get_preprocessor(cls, dataset_name: str, params: Dict) -> BaseImagePreprocessor:
        preprocessor_class = cls._preprocessors.get(dataset_name)
        if not preprocessor_class:
            raise ValueError(f"지원하지 않는 데이터셋입니다: {dataset_name}")
        return preprocessor_class(params)
