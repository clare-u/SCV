class ModelNotFound(Exception):
    def __init__(self, model_version_id: str):
        self.model_version_id: str = model_version_id
        