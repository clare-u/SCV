import logging

import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torchvision import datasets, transforms
from minio import Minio
from torchsummary import summary
import sys
import os
from typing import List, Dict, Any, Optional, Tuple
from pathlib import Path
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('model_trainer.log')
    ]
)
logger = logging.getLogger(__name__)

from neural_network_builder.exceptions.custom_exceptions import ValidationError
from .datasets.datasets_registry import setup_logger
from .datasets import DatasetRegistry, DatasetFactory, DatasetInfo
from .validators.model_validator import ModelValidator

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from model_test.neural_network_builder.builders import ModelBuilder, ModelCodeGenerator
from model_test.neural_network_builder.parsers.validators import ModelConfig
from .save_minio import save_model_to_minio, minio_host_name, minio_user_name, minio_user_password, minio_model_bucket, \
    minio_api_port

class ModelTrainer:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model_builder = ModelBuilder()
        self.training_history = []
        self.model_layer = None
        self.code_generator = ModelCodeGenerator()

        # 데이터셋 구성 로드
        config_path = os.path.join(current_dir, 'datasets', 'configs', 'datasets.yaml')
        DatasetRegistry.load_dataset_configs(config_path)

        print(f"Using device: {self.device}")

    def save_model_with_info(self, model: nn.Module, version_no: str,
                             dataset_info: DatasetInfo, training_history: List,
                             model_layer: Dict) -> None:
        """모델과 관련 정보를 저장"""
        try:
            # 모델에 메타데이터 추가
            model.dataset_info = dataset_info.to_dict()
            model.training_history = training_history
            model.model_layer = model_layer

            # MinIO에 저장
            save_model_to_minio(model, version_no)
            logger.info(f"모델 저장 성공: {version_no}.pth")

        except Exception as e:
            logger.error(f"모델 저장 실패: {str(e)}")
            raise

    def train_epoch(self, model: nn.Module, train_loader: DataLoader,
                   optimizer: optim.Optimizer, criterion: nn.Module) -> Tuple[float, float]:
        """한 epoch 동안의 학습을 수행"""
        model.train()
        total_loss = 0
        correct = 0
        total = 0

        for batch_idx, (data, target) in enumerate(train_loader):
            data, target = data.to(self.device), target.to(self.device)
            optimizer.zero_grad()
            output = model(data)
            loss = criterion(output, target)
            loss.backward()
            optimizer.step()

            total_loss += loss.item()
            pred = output.argmax(dim=1, keepdim=True)
            correct += pred.eq(target.view_as(pred)).sum().item()
            total += target.size(0)

            if batch_idx % 100 == 0:
                print(f'Train Batch: {batch_idx}/{len(train_loader)}, Loss: {loss.item():.4f}')

        accuracy = 100. * correct / total
        avg_loss = total_loss / len(train_loader)

        return avg_loss, accuracy

    def test_model(self, model: nn.Module, test_loader: DataLoader,
                  criterion: nn.Module) -> Tuple[float, float]:
        """모델 테스트 수행"""
        model.eval()
        test_loss = 0
        correct = 0
        total = 0

        with torch.no_grad():
            for data, target in test_loader:
                data, target = data.to(self.device), target.to(self.device)
                output = model(data)
                test_loss += criterion(output, target).item()
                pred = output.argmax(dim=1, keepdim=True)
                correct += pred.eq(target.view_as(pred)).sum().item()
                total += target.size(0)

        test_loss /= len(test_loader)
        accuracy = 100. * correct / total

        print(f"\n실행결과")
        print(f"{accuracy:.2f}% ({correct}/{total})")

        return test_loss, accuracy

    def train(self, config: ModelConfig) -> Dict[str, Any]:
        """모델 학습 메인 함수"""
        try:
            # 데이터셋 정보 가져오기
            dataset_info = DatasetRegistry.get_dataset_info(config.dataName)

            # 데이터 로더 생성
            train_loader, test_loader = DatasetFactory.create_dataset(
                config.dataName,
                config.dataTrainCnt,
                config.dataTestCnt
            )

            # 모델 생성
            print("모델을 생성중입니다...")
            model = self.model_builder.create_model(config.model_dump())
            model = model.to(self.device)

            # 모델 검증
            validator = ModelValidator()
            validation_result = validator.validate_model(model, config.dataName)
            print(f"모델 검증 결과: {validation_result}")

            # 레이어 간 연결 검증
            validator.check_layer_connections(model, config.dataName)

            # 학습 설정
            criterion = nn.CrossEntropyLoss()
            optimizer = optim.Adam(model.parameters(), lr=0.001)
            epochs = config.dataEpochCnt

            # 학습 수행
            print(f"{epochs} 개의 Epoch에 대한 학습을 시작합니다...")
            best_accuracy = 0
            training_history = []

            for epoch in range(epochs):
                print(f"\nEpoch {epoch + 1}/{epochs}")

                # 학습
                train_loss, train_acc = self.train_epoch(model, train_loader, optimizer, criterion)

                # 테스트
                test_loss, test_acc = self.test_model(model, test_loader, criterion)

                # 결과 기록
                epoch_results = {
                    'epoch': epoch + 1,
                    'train_loss': train_loss,
                    'train_accuracy': train_acc,
                    'test_loss': test_loss,
                    'test_accuracy': test_acc
                }
                training_history.append(epoch_results)

                print(f'Training - Loss: {train_loss:.4f}, Accuracy: {train_acc:.2f}%')
                print(f'Testing  - Loss: {test_loss:.4f}, Accuracy: {test_acc:.2f}%')

            #TODO 고쳐야함

                # 최고 성능 모델 저장
                if test_acc > best_accuracy:
                    best_accuracy = test_acc
                    print(f"\nNew best model with accuracy: {best_accuracy:.2f}%")
                    # 모델에 학습 히스토리 추가
                    model.training_history = training_history
                    model.model_layer = self.model_layer

                    # 모델 레이어 정보 업데이트
                    self.model_layer = {
                        "layers": [{
                            "name": layer.__class__.__name__,
                            **{k: v for k, v in layer.__dict__.items() if not k.startswith('_')}
                        } for layer in config.modelLayerAt.layers],
                        "dataName": config.dataName,
                        "dataTrainCnt": config.dataTrainCnt,
                        "dataTestCnt": config.dataTestCnt,
                        "dataLabelCnt": config.dataLabelCnt,
                        "dataEpochCnt": config.dataEpochCnt
                    }

                    # 모델 저장
                    self.save_model_with_info(
                        model=model,
                        version_no=str(config.versionNo),
                        dataset_info=dataset_info,
                        training_history=training_history,
                        model_layer=self.model_layer
                    )

            return {
                "model_version": config.versionNo,
                "model_layer": self.model_layer,
                "final_train_accuracy": train_acc,
                "final_test_accuracy": test_acc,
                "best_test_accuracy": best_accuracy,
                "training_history": training_history
            }

        except Exception as e:
            logger.error(f"학습 중 오류 발생: {str(e)}")
            raise

    def test_saved_model(self, model_version: str):
        """저장된 모델을 로드하여 테스트하는 메서드"""
        try:
            # MinIO에서 모델 로드
            client = Minio(
                endpoint=f"{minio_host_name}:{minio_api_port}",
                access_key=minio_user_name,
                secret_key=minio_user_password,
                secure=False
            )

            # 임시 파일로 다운로드
            model_path = f"temp_model_{model_version}.pth"
            client.fget_object(minio_model_bucket, f"{model_version}.pth", model_path)

            # 모델 로드 및 평가 모드 설정
            model = torch.load(model_path)
            model = model.to(self.device)
            model.eval()

            # 데이터셋 정보 확인
            if not hasattr(model, 'dataset_info'):
                raise ValueError("모델에 데이터셋 정보가 없습니다")

            # DatasetInfo 객체 복원
            dataset_info = DatasetInfo.from_dict(model.dataset_info)

            # 적절한 데이터셋으로 테스트 데이터 로드
            _, test_loader = DatasetFactory.create_dataset(
                dataset_info.name,
                train_count=None,
                test_count=None
            )

            # 테스트 실행
            criterion = nn.CrossEntropyLoss()
            test_loss, test_accuracy = self.test_model(model, test_loader, criterion)

            # model_layer 정보 가져오기
            model_layer = getattr(model, 'model_layer', self.model_layer)

            # 모델 파이썬 코드로 생성
            model_code = self.code_generator.generate_model_code(
                model=model,
                version_no=model_version,
                dataset_info=model_layer
            )

            # 각 레이어의 파라미터 수 추출
            layer_parameters = []
            for name, layer in model.named_modules():
                if isinstance(layer, (nn.Conv2d, nn.Linear)):
                    params = sum(p.numel() for p in layer.parameters())
                    layer_parameters.append(params)

            # 에폭별 결과 배열
            test_res_per_epoch = {
                'accuracy': [res['test_accuracy'] for res in self.training_history],
                'loss': [res['test_loss'] for res in self.training_history]
            }

            training_history = []
            if hasattr(model, 'training_history'):
                training_history = model.training_history

                # chart.js를 위한 데이터 형식으로 변환
                test_res_per_epoch = {
                    'accuracy': [entry['train_accuracy'] for entry in training_history],
                    'loss': [entry['train_loss'] for entry in training_history]
                }
            else:
                # 히스토리가 없는 경우 현재 테스트 결과만 포함
                test_res_per_epoch = {
                    'accuracy': [test_accuracy],
                    'loss': [test_loss]
                }

            # 임시 파일 삭제
            os.remove(model_path)

            return {
                # "model_version": model_version,
                # "model_structure": model_structure,
                "model_layer": model_layer,
                "model_code": model_code,
                "final_test_accuracy": test_accuracy,
                "final_test_loss": test_loss,
                "train_result_per_epoch": test_res_per_epoch,
                "layer_parameters": layer_parameters,
                "training_history": training_history
            }

        except Exception as e:
            print(f"Error testing model: {str(e)}")
            raise e