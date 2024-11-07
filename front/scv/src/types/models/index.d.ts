import { MemberResponse } from "../member";

// 단일 모델 객체에 대한 타입 정의
export interface Model {
  modelId: number;
  modelName: string;
  dataName: string;
  latestVersion: number;
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
