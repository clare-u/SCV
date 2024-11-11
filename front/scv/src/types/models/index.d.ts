import { MemberResponse } from "../users";
import { UserResponse } from "../users";

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
  confusionMatrix: number[][];
  exampleImg: ExampleImage;
  featureActivation: FeatureActivation[];
  activationMaximization: ActivationMaximization[];
}

// Define nested types for clarity

export interface TrainingInfo {
  training_history: Array<{
    epoch: number;
    test_loss: number;
    train_loss: number;
    test_accuracy: number;
    train_accuracy: number;
  }>;
}

export interface ExampleImage {
  example_image: string;
}

export interface FeatureActivation {
  origin: string;
}

export interface ActivationMaximization {
  image: string;
}
