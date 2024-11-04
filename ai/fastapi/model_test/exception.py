class ModelNotFound(Exception):
    def __init__(self, model_version_id: str):
        self.model_version_id: str = model_version_id


class DataSetNotFound(Exception):
    def __init__(self, dataset: str, kind: str):
        self.id: str = f"{dataset}_{kind}"