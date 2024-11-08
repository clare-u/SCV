class InferenceException(Exception):
    """추론 관련 기본 예외 클래스"""

    def __init__(self, message: str):
        self.message = message
        super().__init__(self.message)


class DataPreprocessException(InferenceException):
    """데이터 전처리 중 발생하는 예외"""
    pass


class ModelLoadException(InferenceException):
    """모델 로드 중 발생하는 예외"""
    pass


class InvalidInputException(InferenceException):
    """잘못된 입력 데이터 예외"""
    pass
