from minio import Minio
from dotenv import load_dotenv
from io import BytesIO
import os
import pickle

from torchvision.datasets import MNIST, FashionMNIST, CIFAR10, SVHN, EMNIST
from torchvision import transforms

from collections import defaultdict

from time import sleep

from torch.utils.data import Subset 
from torch.utils.data import DataLoader


load_dotenv(verbose=True)
minio_user_name=os.getenv("MINIO_ROOT_USER")
minio_user_password=os.getenv("MINIO_ROOT_PASSWORD")
minio_host_name=os.getenv("MINIO_HOST_NAME")
minio_model_bucket=os.getenv("MINIO_MODEL_BUCKET")
minio_dataset_bucket=os.getenv("MINIO_DATASET_BUCKET")
minio_port=os.getenv("MINIO_PORT")

client = Minio("{}:{}".format(minio_host_name, minio_port),
        access_key=minio_user_name,
        secret_key=minio_user_password,
        # SSL 설정 해제
        secure=False
    )

def upload_dataset_to_minio(data, object_name):
    buffer = BytesIO()
    pickle.dump(data, buffer)
    buffer.seek(0)

    client.put_object(
        bucket_name=minio_dataset_bucket,
        object_name=object_name,
        data=buffer,
        length=buffer.getbuffer().nbytes
    )
    print(f"{object_name} 데이터 셋을 업로드 했습니다.")

def upload_cka_dataset_to_minio(test_data, dataset_name):
    indices_per_label = defaultdict(list)
    cnt = 0
    for idx, (image, label) in enumerate(test_data):
        if (len(indices_per_label[label])) < 10:
            indices_per_label[label].append(idx)
            cnt += 1
            if cnt==100:
                break
    
    for indices in indices_per_label.values() :
        print(len(indices))

    if all(len(indices) == 10 for indices in indices_per_label.values()) or cnt == 100:
        selected_indices = [idx for indices in indices_per_label.values() for idx in indices]
        # Subset에 대한 DataLoader 생성
        subset_loader = DataLoader(Subset(test_data, selected_indices), batch_size=1, shuffle=False)

        # Upload the subset
        upload_dataset_to_minio(subset_loader, f"{dataset_name}_cka")
        print(f"{dataset_name}의 cka 데이터 셋을 업로드 했습니다.")




if not client.bucket_exists(minio_model_bucket):
    client.make_bucket(minio_model_bucket)
if not client.bucket_exists(minio_dataset_bucket):
    client.make_bucket(minio_dataset_bucket)


while not client.bucket_exists(minio_dataset_bucket):
    sleep(2)


# MNIST
mnist_transforms = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.1307], std=[0.3081])
])

# FASHION_MNIST
fashion_mnist_transforms = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.2860], std=[0.3530])
])

# CIFAR10
cifar10_transforms = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.4914, 0.4822, 0.4465], std=[0.2470, 0.2435, 0.2616])
])

# SVHN
svhn_transforms = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.4377, 0.4438, 0.4728], std=[0.1980, 0.2010, 0.1970])
])

# EMNIST
emnist_transforms = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.1751], std=[0.3332])
])

# MNIST 데이터셋
train_dataset_mnist = MNIST(root='/data', train=True, download=True, transform=mnist_transforms)
test_dataset_mnist = MNIST(root='/data', train=False, download=True, transform=mnist_transforms)

# DataLoader 설정
train_loader_mnist = DataLoader(train_dataset_mnist, batch_size=64, shuffle=True)  # 배치 크기 64
test_loader_mnist = DataLoader(test_dataset_mnist, batch_size=1, shuffle=False)    # 배치 크기 1

upload_dataset_to_minio(train_loader_mnist, "mnist_train")
upload_dataset_to_minio(test_loader_mnist, "mnist_test")
upload_cka_dataset_to_minio(test_dataset_mnist, "mnist")

# Fashion-MNIST 데이터셋
train_dataset_fashion_mnist = FashionMNIST(root='/data', train=True, download=True, transform=fashion_mnist_transforms)
test_dataset_fashion_mnist = FashionMNIST(root='/data', train=False, download=True, transform=fashion_mnist_transforms)

# DataLoader 설정
train_loader_fashion_mnist = DataLoader(train_dataset_fashion_mnist, batch_size=64, shuffle=True)  # 배치 크기 64
test_loader_fashion_mnist = DataLoader(test_dataset_fashion_mnist, batch_size=1, shuffle=False)    # 배치 크기 1

upload_dataset_to_minio(train_loader_fashion_mnist, "fashion_mnist_train")
upload_dataset_to_minio(test_loader_fashion_mnist, "fashion_mnist_test")
upload_cka_dataset_to_minio(test_dataset_fashion_mnist, "fashion_mnist")

# CIFAR-10 데이터셋
train_dataset_cifar10 = CIFAR10(root='/data', train=True, download=True, transform=cifar10_transforms)
test_dataset_cifar10 = CIFAR10(root='/data', train=False, download=True, transform=cifar10_transforms)

# DataLoader 설정
train_loader_cifar10 = DataLoader(train_dataset_cifar10, batch_size=64, shuffle=True)  # 배치 크기 64
test_loader_cifar10 = DataLoader(test_dataset_cifar10, batch_size=1, shuffle=False)    # 배치 크기 1

upload_dataset_to_minio(train_loader_cifar10, "cifar10_train")
upload_dataset_to_minio(test_loader_cifar10, "cifar10_test")
upload_cka_dataset_to_minio(test_dataset_cifar10, "cifar10")

# SVHN 데이터셋
train_dataset_svhn = SVHN(root='/data', split='train', download=True, transform=svhn_transforms)
test_dataset_svhn = SVHN(root='/data', split='test', download=True, transform=svhn_transforms)

# DataLoader 설정
train_loader_svhn = DataLoader(train_dataset_svhn, batch_size=64, shuffle=True)  # 배치 크기 64
test_loader_svhn = DataLoader(test_dataset_svhn, batch_size=1, shuffle=False)    # 배치 크기 1

upload_dataset_to_minio(train_loader_svhn, "svhn_train")
upload_dataset_to_minio(test_loader_svhn, "svhn_test")
upload_cka_dataset_to_minio(test_dataset_svhn, "svhn")

# EMNIST 데이터셋
train_dataset_emnist = EMNIST(root='/data', split='letters', train=True, download=True, transform=emnist_transforms)
test_dataset_emnist = EMNIST(root='/data', split='letters', train=False, download=True, transform=emnist_transforms)

# DataLoader 설정
train_loader_emnist = DataLoader(train_dataset_emnist, batch_size=64, shuffle=True)  # 배치 크기 64
test_loader_emnist = DataLoader(test_dataset_emnist, batch_size=1, shuffle=False)    # 배치 크기 1

upload_dataset_to_minio(train_loader_emnist, "emnist_train")
upload_dataset_to_minio(test_loader_emnist, "emnist_test")
upload_cka_dataset_to_minio(test_dataset_emnist, "emnist")





