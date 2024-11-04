class ModelBuilderError(Exception):
    """Base exception for all model builder errors"""
    pass

class JSONParsingError(ModelBuilderError):
    """JSON 파싱 오류"""
    def __init__(self, message: str, json_data: str = None):
        self.message = message
        self.json_data = json_data
        super().__init__(self.message)

class LayerConfigError(ModelBuilderError):
    """Raised when there is an error in the layer configuration"""
    def __init__(self, layer_type: str, missing_params: list = None):
        self.layer_type = layer_type
        self.missing_params = missing_params or []
        message = f"Invalid configuration for layer type '{layer_type}'"
        if missing_params:
            message += f": missing required parameters {missing_params}"
        super().__init__(message)

class UnsupportedLayerError(ModelBuilderError):
    """Raised when an unsupported layer type is encountered"""
    def __init__(self, layer_type: str):
        self.layer_type = layer_type
        message = f"Unsupported layer type: {layer_type}"
        super().__init__(message)

class BuilderError(ModelBuilderError):
    """Raised when there is an error building the model or layer"""
    def __init__(self, message: str, layer_config: dict = None):
        self.message = message
        self.layer_config = layer_config
        super().__init__(self.message)