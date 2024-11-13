import { MemberResponse } from "../users";
import { UserResponse } from "../users";
import { Dataset } from "../dataset";

// 단일 모델 객체에 대한 타입 정의
export interface Model {
  modelId: number;
  modelName: string;
  dataName: string;
  latestVersion: number;
  latestVersionId: number;
  accuracy: number;
  createdAt: string;
  updatedAt: string;
  userProfile: MemberResponse;
}

// Content의 루트 객체에 대한 타입 정의
export interface Content {
  content: Model[];
  pageable: Pageable;
}

// Pageable 객체에 대한 타입 정의
export interface Pageable {
  pageNumber: number;
  pageSize: number;
}

// queryParams 정의
export interface ModelQueryParams {
  page: number;
  size: number;
  orderBy?: "updatedAt" | "createdAt";
  direction?: "asc" | "desc";
  dataName?: string;
  modelName?: string;
}

// myModel
export interface MyModel {
  title: string; // 모델 이름
  modelId: number;
  modelVersionId: number; // 모델 버전 ID
  version: number; // 버전 번호
  dataName: string; // 데이터셋 이름
  accuracy: number; // 정확도 값
  createdAt: string; // 생성 일자 (ISO 형식 문자열)
  updatedAt: string; // 업데이트 일자 (ISO 형식 문자열)
}

export interface MyModelList {
  content: MyModel[];
  pageable: Pageable;
}

// 모델의 버전들 조회
export interface ModelVersion {
  versionId: number;
  versionNo: number;
}

export interface ModelVersions {
  userInfo: UserResponse;
  modelId: number;
  modelName: string;
  DataName: string;
  latestVersion: number;
  modelVersions: ModelVersion[];
  createdAt: string;
  updatedAt: string;
}

// 모델 버전 상세 조회
export interface TrainInfo {
  trainLoss: number | null;
  trainAccuracy: number | null;
}

export interface resultResponseWithImages {
  codeView: string;
  testAccuracy: number;
  testLoss: number;
  trainInfos: TrainInfo;
  totalParams: number;
  layerParams: string;
  confusionMatrix: string;
  exampleImg: string;
}

export interface Layer {
  name: string;
  in_channels?: number;
  out_channels?: number;
  kernel_size?: number;
  stride?: number;
  in_features?: number;
  out_features?: number;
  dim?: number;
}
export interface VersionResponse {
  modelVersionId: number;
  layers: Layer[];
  resultResponseWithImages: ResultResponseWithImages;
}

// Define the ResultResponseWithImages interface, which includes various detailed fields
export interface ResultResponseWithImages {
  modelId: number;
  modelVersionId: number;
  codeView: string;
  testAccuracy: number;
  testLoss: number;
  totalParams: number;
  trainInfos: TrainingInfo;
  layerParams: number[];
  confusionMatrix: string;
  exampleImg: ExampleImages;
  featureActivation: FeatureActivation[];
  activationMaximization: ActivationMaximization;
}

export interface TrainingInfo {
  training_history: Array<{
    epoch: number;
    test_loss: number;
    train_loss: number;
    test_accuracy: number;
    train_accuracy: number;
  }>;
}

// 이미지 데이터를 포함하는 객체 구조의 타입 정의
export interface ImageData {
  conf: number; // 신뢰도 값
  image: number[][][][]; // 4차원 배열 형태의 이미지 데이터
}

// ExampleImages 데이터의 전체 구조 타입 정의
export interface ExampleImages {
  [key: string]: ImageData | null; // 각 이미지의 키는 문자열이며, 값은 ImageData 또는 null
}

export interface FeatureActivation {
  origin: string;
  visualize: string;
}

export interface LayerFeatureMaps {
  layerName: string; // 레이어 이름
  featureMaps: string[]; // 해당 레이어의 특징 맵 이미지들 (base64 인코딩된 문자열 배열)
}

type ActivationImage = number[][];

// 단일 활성화 데이터 샘플을 위한 인터페이스
interface ActivationMaximization {
  // 28x28 크기의 이미지 데이터
  image: ActivationImage;
  // MNIST 데이터셋의 레이블 (0-9)
  label: string;
}

// 모델 생성
export interface ModelRequest {
  dataName: Dataset;
  modelName: string;
}

export interface ModelResponse {
  modelId: number;
  modelVersionId: number;
}

// 모델 버전 저장
export interface ModelVersionRequest {
  model_version_id: number;
  layers: Layer[];
}

// 모델 실행
export interface RunResponse {
  modelId: number;
  modelVersionId: number;
  codeView: string;
  testAccuracy: number;
  testLoss: number;
  totalParams: number;
  trainInfos: string; // JSON 문자열 형식
  layerParams: string; // JSON 문자열 형식
}
