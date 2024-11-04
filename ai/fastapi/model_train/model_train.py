import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torchvision import datasets, transforms
from minio import Minio
from torchsummary import summary
import sys
import os
from .datasets import DatasetRegistry, DatasetFactory

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from model_test.neural_network_builder.builders.model_builder import ModelBuilder
from model_test.neural_network_builder.parsers.validators import ModelConfig
from .save_minio import save_model_to_minio, minio_host_name, minio_user_name, minio_user_password, minio_model_bucket, \
    minio_api_port


class ModelTrainer:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model_builder = ModelBuilder()
        self.training_history = []
        self.model_layer = None

        # 데이터셋 구성 로드
        config_path = os.path.join(current_dir, 'datasets', 'configs', 'datasets.yaml')
        DatasetRegistry.load_dataset_configs(config_path)

        print(f"Using device: {self.device}")

    def load_data(self, batch_size: int):
        transform = transforms.Compose([
            transforms.ToTensor(),
            transforms.Normalize((0.1307,), (0.3081,))
        ])

        train_dataset = datasets.MNIST('data', train=True, download=True, transform=transform)
        test_dataset = datasets.MNIST('data', train=False, transform=transform)

        train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
        test_loader = DataLoader(test_dataset, batch_size=batch_size)

        return train_loader, test_loader

    def train_epoch(self, model, train_loader, optimizer, criterion):
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

    def test_model(self, model, test_loader, criterion):
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

        # rough한 결과 출력 ( 피그마처럼 )
        print(f"\n실행결과")
        print(f"{accuracy:.2f}% ({correct}/{total})")

        return test_loss, accuracy

    def train(self, config: ModelConfig):
        """모델 학습 메인 함수"""
        # 학습 하이퍼파라미터 설정
        batch_size = 64
        learning_rate = 0.001
        epochs = config.dataEpochCnt

        # 데이터셋 정보 가져오기
        dataset_info = DatasetRegistry.get_dataset_info(config.dataName)

        # 입력 레이어의 채널 수 확인
        first_layer = config.modelLayerAt.layers[0]
        if hasattr(first_layer, 'in_channels') and first_layer.in_channels != dataset_info.input_shape[0]:
            raise ValueError(
                f"Model input channels ({first_layer.in_channels}) "
                f"does not match dataset channels ({dataset_info.input_shape[0]})"
            )

        # 데이터 로드
        train_loader, test_loader = DatasetFactory.create_dataset(
            config.dataName,
            config.dataTrainCnt,
            config.dataTestCnt
        )

        # 모델 생성
        print("Creating model...")
        model = self.model_builder.create_model(config.model_dump())
        model = model.to(self.device)

        # 학습 설정
        criterion = nn.CrossEntropyLoss()
        optimizer = optim.Adam(model.parameters(), lr=learning_rate)

        # 학습 수행
        print(f"Starting training for {epochs} epochs...")
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

            # 최고 성능 모델 저장
            if test_acc > best_accuracy:
                best_accuracy = test_acc
                print(f"New best model with accuracy: {best_accuracy:.2f}%")
                # 모델에 학습 히스토리 추가
                model.training_history = training_history
                save_model_to_minio(model, str(config.versionNo))

            model_structure = str(model)
            model_layer = {
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

        return {
            "model_version": config.versionNo,
            "model_structure": model_structure,
            "model_layer": model_layer,
            "final_train_accuracy": train_acc,
            "final_test_accuracy": test_acc,
            "best_test_accuracy": best_accuracy,
            "training_history": training_history
        }

    def generate_model_code(self, model):
        """모델을 Python 코드로 변환하는 헬퍼 함수"""
        code_lines = []
        code_lines.append("import torch.nn as nn\n")
        code_lines.append("class Model(nn.Module):")
        code_lines.append("    def __init__(self):")
        code_lines.append("        super(Model, self).__init__()")

        # 레이어 정의 추가
        for i, layer in enumerate(model.children()):
            layer_str = f"        self.layer{i} = {layer.__class__.__name__}("
            # 레이어 파라미터 추가
            params = []
            for name, param in layer.__dict__.items():
                if not name.startswith('_'):
                    params.append(f"{name}={param}")
            layer_str += ", ".join(params) + ")"
            code_lines.append(layer_str)

        # forward 함수 추가
        code_lines.append("\n    def forward(self, x):")
        for i in range(len(list(model.children()))):
            code_lines.append(f"        x = self.layer{i}(x)")
        code_lines.append("        return x")

        return "\n".join(code_lines)

    def test_saved_model(self, model_version: str):
        """저장된 모델을 로드하여 테스트하는 메서드"""
        try:
            # 데이터 로드
            _, test_loader = self.load_data(batch_size=64)

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

            # 테스트 실행
            criterion = nn.CrossEntropyLoss()
            test_loss, test_accuracy = self.test_model(model, test_loader, criterion)

            # 모델구조
            from torchsummary import summary
            model_structure = str(model)
            # 모델코드 (Python코드로 변환)
            model_code = self.generate_model_code(model)

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
                "model_version": model_version,
                "model_structure": model_structure,
                "model_layer": self.model_layer if hasattr(self, 'model_layer') else None,
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