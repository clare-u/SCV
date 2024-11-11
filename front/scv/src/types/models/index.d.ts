import { MemberResponse } from "../users";
import { UserResponse } from "../users";

// 단일 모델 객체에 대한 타입 정의
export interface Model {
  modelId: number;
  modelName: string;
  dataName: string;
  latestVersion: number;
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
