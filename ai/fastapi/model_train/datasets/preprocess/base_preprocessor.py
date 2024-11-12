from abc import ABC, abstractmethod
from typing import Dict

import cv2
import numpy as np


class BaseImagePreprocessor(ABC):
    """기본 이미지 전처리 추상 클래스"""

    def __init__(self, params: Dict):
        self.params = params

    @abstractmethod
    def preprocess(self, image: np.ndarray) -> np.ndarray:
        pass

    def _normalize_image(self, image: np.ndarray) -> np.ndarray:
        return cv2.normalize(image, None, 0, 255, cv2.NORM_MINMAX)

    def _convert_to_grayscale(self, image: np.ndarray) -> np.ndarray:
        if len(image.shape) == 3:
            return cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        return image
