import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torchvision import datasets, transforms
import sys
import os

current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
sys.path.append(root_dir)

from model_test.neural_network_builder.builders.model_builder import ModelBuilder
from model_test.neural_network_builder.parsers.validators import ModelConfig
from save_minio import save_model_to_minio, minio_host_name, minio_user_name, minio_user_password, minio_model_bucket


class ModelTrainer:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model_builder = ModelBuilder()
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

        return test_loss, accuracy

    def train(self, config: ModelConfig):
        """모델 학습 메인 함수"""
        # 학습 하이퍼파라미터 설정
        batch_size = 64
        learning_rate = 0.001
        epochs = config.dataEpochCnt

        # 데이터 로드
        print("Loading MNIST dataset...")
        train_loader, test_loader = self.load_data(batch_size)

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
                save_model_to_minio(model, str(config.versionNo))

        return {
            "model_version": config.versionNo,
            "final_train_accuracy": train_acc,
            "final_test_accuracy": test_acc,
            "best_test_accuracy": best_accuracy,
            "training_history": training_history
        }

    def test_saved_model(self, model_version: str):
        """저장된 모델을 로드하여 테스트하는 메서드"""
        try:
            # 데이터 로드
            _, test_loader = self.load_mnist_data(batch_size=64)

            # MinIO에서 모델 로드
            client = Minio(
                endpoint=minio_host_name,
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

            # 모델 구조 분석
            from torchsummary import summary
            summary_result = summary(model, (1, 28, 28))  # MNIST 이미지 크기

            # 각 레이어의 파라미터 수 추출
            layer_parameters = []
            for name, layer in model.named_modules():
                if isinstance(layer, (nn.Conv2d, nn.Linear)):
                    params = sum(p.numel() for p in layer.parameters())
                    layer_parameters.append(params)

            # 임시 파일 삭제
            os.remove(model_path)

            return {
                "dataset_name": "MNIST",
                "test_accuracy": test_accuracy,
                "test_loss": test_loss,
                "layer_parameters": layer_parameters
            }

        except Exception as e:
            print(f"Error testing model: {str(e)}")
            raise e