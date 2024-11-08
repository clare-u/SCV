import logging
from pathlib import Path
from typing import Dict, List

import cv2

"""
pip install opencv-python
pip install opencv-python-headless 로 다운받기
"""
import numpy as np
import torch
from PIL import Image
from torchvision import transforms

from model_train.inference.exceptions import InvalidInputException, DataPreprocessException

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

        logger.info(f"데이터세트 구성이 로드되었습니다:")
        logger.info(f"입력 형태: {self.input_shape}")
        logger.info(f"평균: {self.mean}, 표준: {self.std}")

        self.transform = self._create_transform()

    def _create_transform(self) -> transforms.Compose:
        """데이터셋 정보에 기반한 변환 파이프라인 생성"""
        transform_list = [
            transforms.Resize(self.input_shape[1:], antialias=True),
            transforms.CenterCrop(self.input_shape[1:]),
        ]

        # 채널 수에 따르 변환 추가
        if self.num_channels == 1:  # MNIST, FASHION_MNIST, EMNIST
            transform_list.extend([
                transforms.Grayscale(num_output_channels=1),
                transforms.ToTensor(),
                transforms.Lambda(lambda x: 1 - x),  # 이미지 반전
                transforms.Lambda(lambda x: (x > 0.5).float()),  # 이진화
            ])
        else:  # CIFAR10, SVHN
            transform_list.extend([
                transforms.ToTensor(),
            ])

        # 데이터셋별 정규화 적용
        transform_list.append(transforms.Normalize(self.mean, self.std))

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

    def _preprocess_single_channel(self, image_path: str) -> np.ndarray:
        """흑백 이미지 전처리 (MNIST류 데이터셋용)"""
        # 이미지 로드 및 그레이스케일 변환
        img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
        if img is None:
            raise DataPreprocessException("이미지를 로드할 수 없습니다")

        # 노이즈 제거
        img = cv2.GaussianBlur(img, (5, 5), 0)

        # Otsu's 이진화로 자동 임계값 설정
        _, img = cv2.threshold(img, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

        # 윤곽선 찾기
        contours, _ = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        if contours:
            # 가장 큰 윤곽선 선택
            main_contour = max(contours, key=cv2.contourArea)
            x, y, w, h = cv2.boundingRect(main_contour)

            # 여백 추가
            padding = int(max(w, h) * 0.1)  # 동적 패딩
            x = max(0, x - padding)
            y = max(0, y - padding)
            w = min(img.shape[1] - x, w + 2 * padding)
            h = min(img.shape[0] - y, h + 2 * padding)

            # 숫자 영역 추출
            img = img[y:y + h, x:x + w]

            # 정사각형 만들기
            target_size = max(w, h)
            square_img = np.zeros((target_size, target_size), dtype=np.uint8)
            x_offset = (target_size - w) // 2
            y_offset = (target_size - h) // 2
            square_img[y_offset:y_offset + h, x_offset:x_offset + w] = img

            # 목표 크기로 조정
            img = cv2.resize(square_img, (self.input_shape[1], self.input_shape[2]))

        return img

    def _preprocess_multi_channel(self, image_path: str) -> np.ndarray:
        """컬러 이미지 전처리 (CIFAR10, SVHN용)"""
        img = cv2.imread(image_path)
        if img is None:
            raise DataPreprocessException("이미지를 로드할 수 없습니다")

        # BGR to RGB
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

        # 목표 크기로 조정
        img = cv2.resize(img, (self.input_shape[1], self.input_shape[2]))

        return img

    def process_image(self, image_path: str) -> torch.Tensor:
        """이미지 로드하고 모델입력에 맞게 전처리하는함수"""
        try:
            # 이미지 검증
            self.validate_image(image_path)

            # 이미지 로드
            image = Image.open(image_path)

            # 전처리
            tensor = self.transform(image)

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
