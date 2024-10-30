class InvalidModelId(Exception):
    def __init__(self, model_version_layer_id: str):
        self.model_version_layer_id: str = model_version_layer_id

class LayerNotFound(Exception):
    def __init__(self):
        pass