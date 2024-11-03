import requests
import json


def test_model_training():
    # API 엔드포인트 설정
    model_id = "mnist-classifier"
    version_id = "1"
    url = f"http://localhost:8000/api/v1/models/{model_id}/versions/{version_id}"

    # 모델 설정
    config = {
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
                    "in_features": 5408,  # 계산된 크기: 32 * 13 * 13
                    "out_features": 10
                }
            ]
        },
        "dataName": "MNIST",
        "dataTrainCnt": 50000,
        "dataTestCnt": 10000,
        "dataLabelCnt": 10,
        "dataEpochCnt": 2  # 테스트를 위해 적은 epoch 사용
    }

    try:
        print("Sending request to train model...")
        response = requests.post(url, json=config)
        response.raise_for_status()

        result = response.json()

        print("\nTraining Results:")
        print(json.dumps(result, indent=2))

        print(f"\nModel saved with version: {version_id}")
        print(f"Final test accuracy: {result.get('final_test_accuracy', 'N/A')}%")
        print(f"Best test accuracy: {result.get('best_test_accuracy', 'N/A')}%")

    except requests.exceptions.RequestException as e:
        print(f"Error occurred: {e}")
        if hasattr(e, 'response'):
            print(f"Error details: {e.response.text}")


if __name__ == "__main__":
    test_model_training()