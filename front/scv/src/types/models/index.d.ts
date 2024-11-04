// 단일 모델 객체에 대한 타입 정의
export interface Model {
  modelId: number;
  modelName: string;
  dataName: string;
  latestNumber: number;
  createdAt: string;
  updatedAt: string;
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
  orderBy?: "updatedAt";
  direction?: "asc" | "desc";
  dataName?: string;
  modelName?: string;
}
