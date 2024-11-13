class ModelBuilderError(Exception):
    """모델 빌더 오류의 기본 예외 클래스"""
    pass

class JSONParsingError(ModelBuilderError):
    """JSON 파싱 오류"""
    def __init__(self, message: str, json_data: str = None):
        self.message = message
        self.json_data = json_data
        super().__init__(self.message)

class LayerConfigError(ModelBuilderError):
    """레이어 구성에서 오류가 발생할 때 발생하는 예외"""
    def __init__(self, layer_type: str, missing_params: list = None):
        self.layer_type = layer_type
        self.missing_params = missing_params or []
        message = f"레이어 타입 '{layer_type}'의 구성 오류"
        if missing_params:
            message += f": 필수 파라미터가 누락되었습니다 {missing_params}"
        super().__init__(message)

class UnsupportedLayerError(ModelBuilderError):
    """지원되지 않는 레이어 타입이 사용될 때 발생하는 예외"""
    def __init__(self, layer_type: str):
        self.layer_type = layer_type
        message = f"지원되지 않는 레이어 타입: {layer_type}"
        super().__init__(message)

class BuilderError(ModelBuilderError):
    """모델 또는 레이어 생성 시 오류가 발생할 때 발생하는 예외"""
    def __init__(self, message: str, layer_config: dict = None):
        self.message = message
        self.layer_config = layer_config
        super().__init__(self.message)

# Validation 관련 예외 추가
class ValidationError(ModelBuilderError):
    """모델 검증 관련 기본 예외 클래스"""
    def __init__(self, message: str, layer_info: dict = None):
        self.message = message
        self.layer_info = layer_info or {}
        super().__init__(self.message)

class ArchitectureError(ValidationError):
    """모델 아키텍처 검증 실패시 발생하는 예외"""
    def __init__(self, message: str, layer_details: dict = None):
        super().__init__(f"아키텍처 검증 실패: {message}", layer_details)

class InputShapeError(ValidationError):
    """입력 shape이 데이터셋과 맞지 않을 때 발생하는 예외"""
    def __init__(self, expected_shape, actual_shape):
        self.expected_shape = expected_shape
        self.actual_shape = actual_shape
        super().__init__(f"입력 shape 불일치 - 예상: {expected_shape}, 실제: {actual_shape}")

class LayerConnectionError(ValidationError):
    """레이어 간 연결이 맞지 않을 때 발생하는 예외"""
    def __init__(self, layer_name: str, input_shape, output_shape, message: str):
        self.layer_name = layer_name
        self.input_shape = input_shape
        self.output_shape = output_shape
        self.message = message
        super().__init__(f"레이어 '{layer_name}' 연결 오류 - {message}")

class ConfigurationError(ModelBuilderError):
    """설정 관련 오류가 발생할 때 발생하는 예외"""
    def __init__(self, message: str, config_details: dict = None):
        self.message = message
        self.config_details = config_details or {}
        super().__init__(self.message)