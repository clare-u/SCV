import cv2
import numpy as np

from datasets.preprocess.base_preprocessor import BaseImagePreprocessor


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


class CIFAR10Preprocessor(BaseImagePreprocessor):
    """CIFAR10 데이터셋 전처리"""

    def preprocess(self, image: np.ndarray) -> np.ndarray:
        # RGB 이미지 검증
        if len(image.shape) != 3:
            raise ValueError("CIFAR10은 RGB 이미지가 필요합니다")

        # CIFAR10 표준 크기(32x32)로 리사이즈
        image = cv2.resize(image, (32, 32))

        # BGR을 RGB로 변환 (OpenCV는 BGR을 사용)
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # 노이즈 제거 (선택적)
        if self.params.get('noise_reduction', True):
            image = cv2.fastNlMeansDenoisingColored(
                image,
                None,
                10,  # h (필터링 강도)
                10,  # hColor
                7,  # templateWindowSize
                21  # searchWindowSize
            )

        # 대비 개선
        alpha = self.params.get('contrast_alpha', 1.2)  # 대비
        beta = self.params.get('contrast_beta', 10)  # 밝기
        image = cv2.convertScaleAbs(image, alpha=alpha, beta=beta)

        # 채널별 정규화
        for i in range(3):  # RGB 각 채널
            image[:, :, i] = cv2.normalize(
                image[:, :, i],
                None,
                0, 255,
                cv2.NORM_MINMAX
            )

        # uint8로 변환
        image = image.astype(np.uint8)

        # RGB -> 그레이스케일로 변환 (contour 검출용)
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)

        return gray  # 그레이스케일 이미지 반환

    def _enhance_colors(self, image: np.ndarray) -> np.ndarray:
        """색상 향상을 위한 보조 메서드"""
        # HSV 색상 공간에서 채도(S)와 명도(V) 조정
        hsv = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)
        hsv[:, :, 1] = cv2.multiply(hsv[:, :, 1], 1.2)  # 채도 증가
        hsv[:, :, 2] = cv2.multiply(hsv[:, :, 2], 1.1)  # 명도 증가
        return cv2.cvtColor(hsv, cv2.COLOR_HSV2RGB)


class SVHNPreprocessor(BaseImagePreprocessor):
    """SVHN(Street View House Numbers) 데이터셋 전처리기"""

    def preprocess(self, image: np.ndarray) -> np.ndarray:
        """SVHN 이미지 전처리 - 숫자를 더 뚜렷하게"""
        if len(image.shape) != 3:
            raise ValueError("SVHN은 RGB 이미지가 필요합니다")

        # SVHN 표준 크기(32x32)로 리사이즈
        image = cv2.resize(image, (32, 32))

        # BGR을 RGB로 변환
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # 노이즈 제거
        denoised = cv2.fastNlMeansDenoisingColored(
            image, None,
            h=self.params.get('denoise_strength', 10),
            hColor=10,
            templateWindowSize=7,
            searchWindowSize=21
        )

        # LAB 색상 공간에서 대비 개선
        lab = cv2.cvtColor(denoised, cv2.COLOR_RGB2LAB)
        l, a, b = cv2.split(lab)

        # CLAHE로 명도 채널 개선
        clahe = cv2.createCLAHE(
            clipLimit=self.params.get('clahe_clip_limit', 2.0),
            tileGridSize=(8, 8)
        )
        l = clahe.apply(l)

        # 채널 병합
        enhanced = cv2.merge((l, a, b))
        enhanced = cv2.cvtColor(enhanced, cv2.COLOR_LAB2RGB)

        # 엣지 강화
        gray = cv2.cvtColor(enhanced, cv2.COLOR_RGB2GRAY)
        edges = cv2.Canny(
            gray,
            self.params.get('edge_low', 50),
            self.params.get('edge_high', 150)
        )
        edges = cv2.dilate(edges, None)

        # 그레이스케일로 변환
        gray = cv2.cvtColor(enhanced, cv2.COLOR_RGB2GRAY)

        return gray


class EMNISTPreprocessor(BaseImagePreprocessor):
    """EMNIST(Extended MNIST) 데이터셋 전처리기"""

    def preprocess(self, image: np.ndarray) -> np.ndarray:
        """EMNIST 이미지 전처리 - 알파벳 문자 강조"""
        # 흑백 변환
        image = self._convert_to_grayscale(image)

        # 이미지 정규화 (대비 향상)
        image = self._normalize_image(image)

        # 노이즈 제거를 위한 가우시안 블러
        kernel_size = self.params.get('blur_kernel', 3)
        image = cv2.GaussianBlur(image, (kernel_size, kernel_size), 0)

        # 배경과 문자 분석
        mean_brightness = np.mean(image)
        is_dark_background = mean_brightness < 127

        # 이진화 처리
        if not is_dark_background:  # 밝은 배경인 경우
            _, image = cv2.threshold(
                image, 0, 255,
                cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU
            )
        else:  # 어두운 배경인 경우
            _, image = cv2.threshold(
                image, 0, 255,
                cv2.THRESH_BINARY + cv2.THRESH_OTSU
            )

        # 문자 방향 보정 (EMNIST 특화)
        if self.params.get('rotation_correction', True):
            coords = np.column_stack(np.where(image > 0))
            if len(coords) > 0:
                angle = cv2.minAreaRect(coords.astype(np.float32))[-1]
                if angle < -45:
                    angle = 90 + angle
                if angle != 0:
                    (h, w) = image.shape[:2]
                    center = (w // 2, h // 2)
                    M = cv2.getRotationMatrix2D(center, angle, 1.0)
                    image = cv2.warpAffine(
                        image, M, (w, h),
                        flags=cv2.INTER_CUBIC,
                        borderMode=cv2.BORDER_REPLICATE
                    )

        # 최종 검사 및 반전
        final_brightness = np.mean(image)
        if final_brightness > 127:  # 배경이 밝은 경우
            image = 255 - image  # EMNIST 형식으로 반전

        return image