import requests
import json
import time
from pathlib import Path


def test_model_training():
    BASE_URL = "http://localhost:8001"

    test_configs = [
        {
            "modelId": 1,
            "versionId": 1,
            "config": {
                "modelLayerAt": {
                    "layers": [
                        {
                            "name": "Conv2d",
                            "in_channels": 1,
                            "out_channels": 32,
                            "kernel_size": 3
                        },
                        {
                            "name": "ReLU"
                        },
                        {
                            "name": "MaxPool2d",
                            "kernel_size": 2,
                            "stride": 2
                        },
                        {
                            "name": "Flatten"
                        },
                        {
                            "name": "Linear",
                            "in_features": 5408,
                            "out_features": 10
                        }
                    ]
                },
                "dataName": "MNIST",
                "dataTrainCnt": 50000,
                "dataTestCnt": 10000,
                "dataLabelCnt": 10,
                "dataEpochCnt": 2,
                "learning_rate": 0.001,
                "batch_size": 64
            }
        }
    ]

    for test_case in test_configs:
        print(f"\nTesting model version {test_case['versionId']}...")

        try:
            # 모델 학습 요청
            train_url = f"{BASE_URL}/api/v1/models/{test_case['modelId']}/versions/{test_case['versionId']}"
            response = requests.post(train_url, json=test_case['config'])
            response.raise_for_status()

            result = response.json()
            # results 키가 있다면 그 안의 데이터를 사용, 없다면 전체 응답 사용
            train_result = result.get('results', result)

            print("\nTraining Results:")
            print(f"Model Version: {train_result['model_version']}")
            print(f"Final Train Accuracy: {train_result['final_train_accuracy']:.2f}%")
            print(f"Final Test Accuracy: {train_result['final_test_accuracy']:.2f}%")
            print(f"Best Test Accuracy: {train_result['best_test_accuracy']:.2f}%")

            # 학습 히스토리의 마지막 에폭 결과 출력
            if train_result['training_history']:
                last_epoch = train_result['training_history'][-1]
                print(f"\nEpoch {last_epoch['epoch']} Results:")
                print(f"Train Loss: {last_epoch['train_loss']:.4f}")
                print(f"Train Accuracy: {last_epoch['train_accuracy']:.2f}%")
                print(f"Test Loss: {last_epoch['test_loss']:.4f}")
                print(f"Test Accuracy: {last_epoch['test_accuracy']:.2f}%")

            # 학습 히스토리 저장
            history_path = Path(f"training_history_v{test_case['versionId']}.json")
            with open(history_path, 'w') as f:
                json.dump({
                    "model_info": {
                        "version": train_result['model_version'],
                        "structure": train_result['model_structure'],
                        "layer": train_result['model_layer']
                    },
                    "training_results": {
                        "final_train_accuracy": train_result['final_train_accuracy'],
                        "final_test_accuracy": train_result['final_test_accuracy'],
                        "best_test_accuracy": train_result['best_test_accuracy'],
                        "training_history": train_result['training_history']
                    }
                }, f, indent=2)
            print(f"\nTraining history saved to {history_path}")

            # 저장된 모델 테스트
            test_url = f"{BASE_URL}/api/v1/models/{test_case['modelId']}/versions/{test_case['versionId']}/test"
            test_response = requests.get(test_url)
            test_response.raise_for_status()

            test_data = test_response.json()
            # results 키가 있다면 그 안의 데이터를 사용, 없다면 전체 응답 사용
            test_result = test_data.get('results', test_data)

            print("\nTest Results:")
            print(f"Final Test Accuracy: {test_result['final_test_accuracy']:.2f}%")
            print(f"Final Test Loss: {test_result['final_test_loss']:.4f}")

            if 'layer_parameters' in test_result:
                print("\nLayer Parameters:")
                for i, params in enumerate(test_result['layer_parameters']):
                    print(f"Layer {i}: {params:,} parameters")

        except requests.exceptions.RequestException as e:
            print(f"Error during API call: {e}")
            if hasattr(e.response, 'text'):
                print(f"Error details: {e.response.text}")
            continue
        except KeyError as e:
            print(f"Missing key in response: {e}")
            print(f"Response structure: {json.dumps(result, indent=2)}")
            continue
        except Exception as e:
            print(f"Unexpected error: {e}")
            if 'result' in locals():
                print(f"Full response: {result}")
            continue

        time.sleep(2)


if __name__ == "__main__":
    print("Starting API tests...")
    test_model_training()