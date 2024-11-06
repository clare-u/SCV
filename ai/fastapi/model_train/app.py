# 모델 학습 요청을 받을 FastAPI
import logging

from fastapi import FastAPI, HTTPException, Path
import sys
import os
from typing import Dict, Any, Optional
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),  # 콘솔 출력
        logging.FileHandler('app.log')  # 파일 출력
    ]
)
logger = logging.getLogger(__name__)

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from model_test.neural_network_builder.parsers.validators import ModelConfig, ModelLayerConfig, layer_classes
try:
    from .model_run import ModelTrainer  # 상대 경로 import
    from .save_minio import save_model_to_minio  # save_model 파일명이 save_minio로 변경
except ImportError:
    # 상대 경로 import가 실패하면 절대 경로로 시도
    from model_train import ModelTrainer
    from save_minio import save_model_to_minio
app = FastAPI()


@app.post("/api/v1/models/{modelId}/versions/{versionId}/train")
async def train_model(
    modelId: int = Path(..., title="Model ID", description="모델 ID"),
    versionId: int = Path(..., title="Version ID", description="모델 버전 ID"),
    config: Dict[str, Any] = None
):
    """모델 학습 엔드포인트"""
    try:
        if config is None:
            raise HTTPException(status_code=400, detail="Config is required")

        logger.info(f"Received config: {config}")

        # 기본 설정 검증
        required_fields = ['modelLayerAt', 'dataName', 'dataTrainCnt', 'dataTestCnt', 'dataLabelCnt', 'dataEpochCnt']
        missing_fields = [field for field in required_fields if field not in config]
        if missing_fields:
            raise HTTPException(
                status_code=422,
                detail=f"Required fields missing: {', '.join(missing_fields)}"
            )

        try:
            # modelLayerAt의 layers를 Layer 객체로 변환
            if isinstance(config['modelLayerAt'], dict) and 'layers' in config['modelLayerAt']:
                processed_layers = []
                for layer_config in config['modelLayerAt']['layers']:
                    layer_type = layer_config.get('name')
                    if not layer_type or layer_type not in layer_classes:
                        raise ValueError(f"Unsupported layer type: {layer_type}")

                    layer_dict = {"name": layer_type}
                    layer_class = layer_classes[layer_type]
                    valid_fields = layer_class.__annotations__.keys()

                    for field in valid_fields:
                        if field == 'name':
                            continue
                        if field in layer_config:
                            layer_dict[field] = layer_config[field]
                        elif field == 'padding' and layer_type == 'Conv2d':
                            layer_dict[field] = 1  # Conv2d의 padding 기본값을 1로 설정
                        elif field == 'stride' and layer_type == 'Conv2d':
                            layer_dict[field] = 1  # Conv2d의 stride 기본값을 1로 설정

                    # Layer 인스턴스 생성
                    layer = layer_class(**layer_dict)
                    processed_layers.append(layer)

                # config['modelLayerAt'] = {'layers': layers}
                config['modelLayerAt']['layers'] = processed_layers

            # ModelConfig 생성 및 검증
            model_config = ModelConfig(
                modelLayerAt=ModelLayerConfig(**config['modelLayerAt']),
                dataName=config['dataName'].upper(),
                dataTrainCnt=config['dataTrainCnt'],
                dataTestCnt=config['dataTestCnt'],
                dataLabelCnt=config['dataLabelCnt'],
                dataEpochCnt=config['dataEpochCnt'],
                versionNo=versionId
            )

            trainer = ModelTrainer()
            result = trainer.train(model_config)

            return {
                "status": "success",
                "modelId": modelId,
                "versionId": versionId,
                **result
            }


        except ValueError as ve:
            logger.error(f"Validation error: {str(ve)}")
            raise HTTPException(status_code=422, detail=str(ve))


    except HTTPException as he:
        logger.error(f"HTTP Exception: {str(he)}")
        raise he
    except Exception as e:
        logger.error(f"Unexpected error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/api/v1/models/{modelId}/versions/{versionId}/test")
async def test_model(
        modelId: int = Path(..., title="Model ID"),
        versionId: int = Path(..., title="Version ID")
):
    """모델 테스트 엔드포인트"""
    try:
        trainer = ModelTrainer()
        result = trainer.test_saved_model(str(versionId))
        logger.info(f"Test completed successfully for version {versionId}")  # 로깅 추가

        return {
            # "status": "success",
            # "modelId": modelId,
            # "versionId": versionId,
            "results": result
        }


    except Exception as e:
        logger.error(f"Error during model testing: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/v1/models/{modelId}/versions/{versionId}")
async def run_model(
        modelId: int = Path(..., title="Model ID", description="모델 ID"),
        versionId: int = Path(..., title="Version ID", description="모델 버전 ID"),
        config: Dict[str, Any] = None
):
    """모델 학습 및 테스트를 연속으로 수행하는 엔드포인트"""
    try:
        logger.info(f"Starting run_model process for model {modelId}, version {versionId}")

        # 1. 학습 수행
        train_result = await train_model(modelId, versionId, config)
        logger.info("Training completed successfully")

        # 2. 테스트 수행
        test_result = await test_model(modelId, versionId)
        logger.info("Testing completed successfully")

        result = {
            "status": "success",
            "modelId": modelId,
            "versionId": versionId,
            "test_results": test_result
        }

        logger.info("run_model completed successfully")
        return result

    except Exception as e:
        error_message = f"run_model 실행 중 에러 발생: {e}"
        logger.error(error_message)

        raise HTTPException(
            status_code=500,
            detail=error_message
        )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8003)