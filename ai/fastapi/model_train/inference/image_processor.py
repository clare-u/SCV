import logging
from pathlib import Path
from typing import Dict, List

import cv2

from model_train.datasets.preprocess.preprocess_config import PreprocessConfig

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
        self.dataset_name = dataset_info.get('name', '')

        # 전처리 설정 로드
        self.preprocess_config = PreprocessConfig()
        self.params = self.preprocess_config.get_params(self.dataset_name)

        logger.info(f"데이터세트 구성이 로드되었습니다:")
        logger.info(f"입력 형태: {self.input_shape}")
        logger.info(f"평균: {self.mean}, 표준: {self.std}")
        logger.info(f"전처리 파라미터: {self.params}")

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

        # 채널 수에 따른 처리
        if self.num_channels == 1:  # MNIST, Fashion_MNIST, EMNIST
            if len(img.shape) == 3:
                img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

            # 노이즈 제거
            kernel_size = self.params['blur_kernel']
            img = cv2.GaussianBlur(img, (kernel_size, kernel_size), 0)

            # 엣지 보존 처리 (Fashion MNIST 등)
            if self.params.get('edge_preserve', False):
                clahe = cv2.createCLAHE(
                    clipLimit=self.params.get('clahe_clip_limit', 2.0),
                    tileGridSize=tuple(self.params.get('clahe_grid_size', (8, 8)))
                )
                img = clahe.apply(img)
                edges = cv2.Canny(
                    img,
                    self.params.get('edge_low', 50),
                    self.params.get('edge_high', 150),
                )
                edge_weight = self.params.get('edge_weight', 0.3)
                img = cv2.addWeighted(img, 1 - edge_weight, edges, edge_weight, 0)

            # 이진화
            threshold_block_size = self.params.get('threshold_block_size', 11)
            threshold_c = self.params.get('threshold_c', 2)
            img = cv2.adaptiveThreshold(
                img, 255,
                cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                cv2.THRESH_BINARY_INV,
                threshold_block_size,
                threshold_c
            )
        else:  # CIFAR10, SVHN (3채널 컬러이미지)
            # 컬러모드 처리
            if self.params.get('color_mode') == 'RGB':
                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

            # 대비 계산
            alpha = self.params.get('contrast_alpha', 1.0)
            beta = self.params.get('contrast_beta', 1.0)
            img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)

            # 노이즈 제거
            if self.params.get('noise_reduction', False):
                denoise_strength = self.params.get('denoise_strength', 10)
                img = cv2.fastNlMeansDenoisingColored(img, None, denoise_strength, denoise_strength)

        if self.num_channels == 1:
            contours, _ = cv2.findContours(
                img,
                cv2.RETR_EXTERNAL,
                cv2.CHAIN_APPROX_SIMPLE,
            )

            if contours:
                # 가장 큰 윤곽선 선택
                main_contour = max(contours, key=cv2.contourArea)
                x, y, w, h = cv2.boundingRect(main_contour)

                # 패딩 추가
                padding_ratio = self.params.get('padding_ratio', 0.2)
                padding = int(max(w, h) * padding_ratio)
                x = max(0, x - padding)
                y = max(0, y - padding)
                w = min(img.shape[1] - x, w + 2 * padding)
                h = min(img.shape[0] - y, h + 2 * padding)

                # 객체 영역 추출
                img = img[y:y + h, x:x + w]

                # EMNIST의 경우 회전 보정
                if self.params.get('rotation_correction', False):
                    # 여기에 회전 보정 로직 추가 가능
                    pass

        # 크기 조정
        target_size = self.input_shape[1:]
        img = cv2.resize(img, target_size)

        return img

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
