import cv2
import numpy as np

from .base_preprocessor import BaseImagePreprocessor


class MNISTPreprocessor(BaseImagePreprocessor):
    """MNIST 데이터셋 전처리"""

    def preprocess(self, image: np.ndarray) -> np.ndarray:
        # 흑백 변환
        image = self._convert_to_grayscale(image)

        # 이미지 정규화 (대비 향상)
        image = self._normalize_image(image)

        # 가우시안 블러로 노이즈 제거
        kernel_size = self.params.get('blur_kernel', 3)
        image = cv2.GaussianBlur(image, (kernel_size, kernel_size), 0)

        # 배경과 숫자 분석
        mean_brightness = np.mean(image)
        is_dark_background = mean_brightness < 127

        if not is_dark_background:  # 밝은 배경인 경우
            # 흰 배경, 검은 글씨를 검은 배경, 흰 글씨로 반전
            _, image = cv2.threshold(
                image, 0, 255,
                cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU
            )
        else:  # 이미 어두운 배경인 경우
            _, image = cv2.threshold(
                image, 0, 255,
                cv2.THRESH_BINARY + cv2.THRESH_OTSU
            )

        # 최종 검사
        final_brightness = np.mean(image)
        if final_brightness > 127:  # 배경이 밝은 경우
            image = 255 - image  # MNIST 형식으로 반전

        return image


class FashionMNISTPreprocessor(BaseImagePreprocessor):
    """Fashion MNIST 데이터셋 전처리"""

    def preprocess(self, image: np.ndarray) -> np.ndarray:
        # 1. 초기 이미지 전처리
        if len(image.shape) == 3:
            # BGR to GRAY 변환
            image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # 2. 이미지 크기 정규화
        target_size = (128, 128)  # 중간 작업을 위한 큰 크기
        image = cv2.resize(image, target_size)

        # 3. 배경 제거를 위한 전처리
        blurred = cv2.GaussianBlur(image, (5, 5), 0)

        # 4. 적응형 임계값으로 배경-의류 분리
        thresh = cv2.adaptiveThreshold(
            blurred,
            255,
            cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
            cv2.THRESH_BINARY_INV,
            11,
            2
        )

        # 5. 노이즈 제거 및 객체 정제
        kernel = np.ones((3, 3), np.uint8)
        mask = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
        mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel)

        # 6. 가장 큰 연결 영역(의류) 찾기
        contours, _ = cv2.findContours(
            mask,
            cv2.RETR_EXTERNAL,
            cv2.CHAIN_APPROX_SIMPLE
        )

        if contours:
            # 가장 큰 윤곽선 선택
            main_contour = max(contours, key=cv2.contourArea)
            mask = np.zeros_like(mask)
            cv2.drawContours(mask, [main_contour], -1, 255, -1)

            # 7. 원본 이미지에서 배경 제거
            image = cv2.bitwise_and(image, image, mask=mask)

            # 8. 바운딩 박스 찾기 및 크롭
            x, y, w, h = cv2.boundingRect(main_contour)
            padding = int(min(w, h) * 0.1)  # 10% 패딩

            # 패딩 추가하면서 이미지 영역 벗어나지 않도록
            x = max(0, x - padding)
            y = max(0, y - padding)
            w = min(image.shape[1] - x, w + 2 * padding)
            h = min(image.shape[0] - y, h + 2 * padding)

            # 의류 영역 크롭
            image = image[y:y + h, x:x + w]

        # 9. Fashion MNIST 크기로 최종 리사이즈
        image = cv2.resize(image, (28, 28))

        # 10. 대비 개선
        clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(4, 4))
        image = clahe.apply(image)

        # 11. 밝기 정규화 (0-255)
        image = cv2.normalize(image, None, 0, 255, cv2.NORM_MINMAX)

        # 12. Fashion MNIST 형식에 맞게 조정
        mean_brightness = np.mean(image)
        if mean_brightness > 127:
            image = 255 - image

        # 13. 부드러운 엣지를 위한 최소한의 블러
        image = cv2.GaussianBlur(image, (3, 3), 0)

        return image

    def _enhance_edges(self, image: np.ndarray) -> np.ndarray:
        """엣지 강화 함수"""
        edges = cv2.Canny(image, 30, 150)
        edges = cv2.dilate(edges, None)
        return cv2.addWeighted(image, 0.9, edges, 0.1, 0)
