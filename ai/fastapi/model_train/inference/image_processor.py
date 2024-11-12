import logging
from pathlib import Path
from typing import Dict, List

import cv2

from datasets.preprocess.preprocess_config import PreprocessConfig
from datasets.preprocess.preprocessor_factory import PreprocessorFactory

"""
pip install opencv-python
pip install opencv-python-headless 로 다운받기
"""
import numpy as np
import torch
from PIL import Image
from torchvision import transforms

from inference.exceptions import InvalidInputException, DataPreprocessException

logger = logging.getLogger(__name__)


class ImageProcessor:
    """사용자가 테스트 할 이미지를 모델 입력에 맞게 전처리"""

    SUPPORTED_FORMATS = ['.png', '.jpg', '.jpeg']
    MAX_IMAGE_SIZE = 1024 * 1024 * 10

    def __init__(self, dataset_info: Dict):
        self.input_shape = tuple(dataset_info['input_shape'])
        self.mean = tuple(dataset_info['mean'])
        self.std = tuple(dataset_info['std'])
        self.num_channels = self.input_shape[0]
        self.dataset_name = dataset_info.get('name', '')

        # 전처리 설정 로드
        self.preprocess_config = PreprocessConfig()
        self.params = self.preprocess_config.get_params(self.dataset_name)

        # 데이터셋별 전처리기 생성
        self.preprocessor = PreprocessorFactory.get_preprocessor(
            self.dataset_name, self.params
        )

        self.transform = self._create_transform()

        logger.info(f"데이터세트 구성이 로드되었습니다:")
        logger.info(f"데이터셋: {self.dataset_name}")
        logger.info(f"입력 형태: {self.input_shape}")
        logger.info(f"평균: {self.mean}, 표준: {self.std}")
        logger.info(f"전처리 파라미터: {self.params}")

    def _create_transform(self) -> transforms.Compose:
        """데이터셋 정보에 기반한 변환 파이프라인 생성"""
        transform_list = []

        # 채널 수에 따르 변환 추가
        if self.num_channels == 1:  # MNIST, FASHION_MNIST, EMNIST
            transform_list.extend([
                transforms.ToTensor(),
                transforms.Normalize(self.mean, self.std)
            ])

        else:  # CIFAR10, SVHN
            transform_list.extend([
                transforms.ToTensor(),
                transforms.Normalize(self.mean, self.std)
            ])

        # 데이터셋별 정규화 적용
        # transform_list.append(transforms.Normalize(self.mean, self.std))

        logger.debug(f"변환 파이프라인 생성: {transform_list}")

        return transforms.Compose(transform_list)

    def validate_image(self, image_path: str) -> None:
        """이미지 유효성 검사"""
        path = Path(image_path)

        # 확장자검사
        if path.suffix.lower() not in self.SUPPORTED_FORMATS:
            raise InvalidInputException(
                f"지원하지 않는 이미지 형식입니다. 지원중인 형식: {self.SUPPORTED_FORMATS}"
            )

        # 파일크기 검사
        if path.stat().st_size > self.MAX_IMAGE_SIZE:
            raise InvalidInputException(
                f"지원하는 파일 크기를 넘었습니다. 최대 지원 크기: {self.MAX_IMAGE_SIZE / 1024 / 1024}MB"
            )

        # 이미지파일 열기 시도
        try:
            with Image.open(image_path) as img:
                img.verify()
        except Exception as e:
            raise InvalidInputException(
                f"손상된 이미지 파일입니다.: {str(e)}"
            )

    def _preprocess_image(self, img: np.ndarray) -> np.ndarray:
        """이미지 전처리"""
        if img is None:
            raise DataPreprocessException("이미지를 불러올 수 없습니다.")

        # 데이터셋별 전처리기를 통한 전처리
        processed_img = self.preprocessor.preprocess(img)

        # 객체 감지 및 크롭
        contours, _ = cv2.findContours(
            processed_img.copy(),
            cv2.RETR_EXTERNAL,
            cv2.CHAIN_APPROX_SIMPLE
        )

        if contours:
            valid_contours = []
            for cnt in contours:
                area = cv2.contourArea(cnt)
                if area > processed_img.shape[0] * processed_img.shape[1] * 0.01:
                    valid_contours.append(cnt)

            if valid_contours:
                main_contour = max(valid_contours, key=cv2.contourArea)
                x, y, w, h = cv2.boundingRect(main_contour)

                padding_ratio = self.params.get('padding_ratio', 0.2)
                size = int(max(w, h) * (1 + padding_ratio * 2))
                center_x = x + w // 2
                center_y = y + h // 2

                left = max(0, center_x - size // 2)
                top = max(0, center_y - size // 2)
                right = min(processed_img.shape[1], left + size)
                bottom = min(processed_img.shape[0], top + size)

                processed_img = processed_img[top:bottom, left:right]

        # 최종 크기 조정
        target_size = self.input_shape[1:]
        processed_img = cv2.resize(processed_img, target_size, interpolation=cv2.INTER_AREA)

        return processed_img

    def process_image(self, image_path: str) -> torch.Tensor:
        """이미지 로드하고 모델입력에 맞게 전처리하는함수"""
        try:
            # 이미지 검증
            self.validate_image(image_path)

            # 이미지 로드
            image = cv2.imread(image_path)

            # 전처리 수행
            processed_image = self._preprocess_image(image)

            # PIL Image로 변환
            if self.num_channels == 1:
                pil_image = Image.fromarray(processed_image)
            else:
                pil_image = Image.fromarray(cv2.cvtColor(processed_image, cv2.COLOR_BGR2RGB))

            # 전처리
            tensor = self.transform(pil_image)

            # 배치 차원 추가
            tensor = tensor.unsqueeze(0)

            logger.info(f"이미지 전처리 완료: shape={tensor.shape}")
            return tensor

        except InvalidInputException as e:
            raise
        except Exception as e:
            raise DataPreprocessException(f"이미지 처리 중 오류 발생: {str(e)}")

    def process_batch(self, image_paths: List[str]) -> List[torch.Tensor]:
        """이미지를 배치로 처리"""
        tensors = []
        for path in image_paths:
            tensor = self.process_image(path)
            tensors.append(tensor)
        return torch.cat(tensors, dim=0)
