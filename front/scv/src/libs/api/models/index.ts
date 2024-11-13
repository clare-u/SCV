import {
  Content,
  ModelQueryParams,
  MyModelList,
  ModelVersions,
  VersionResponse,
  ModelRequest,
  ModelResponse,
  ModelVersionRequest,
  RunResponse,
} from "@/types";
import { handleApiRequest } from "../client";

const DEFAULT_PARAMS: ModelQueryParams = {
  page: 1,
  size: 12,
};

// 모델 리스트를 가져오는 함수
export const fetchModels = async (
  params: ModelQueryParams = DEFAULT_PARAMS,
) => {
  const queryParams = new URLSearchParams();

  // 기본값과 사용자 지정 매개변수 병합
  const finalParams = { ...DEFAULT_PARAMS, ...params };

  // 쿼리 파라미터 생성
  Object.entries(finalParams).forEach(([key, value]) => {
    if (value !== undefined) {
      queryParams.append(key, value.toString());
    }
  });

  // 최종 URL 생성
  const url = `/api/v1/models/?${queryParams.toString()}`;
  console.log("API 요청 URL:", url);
  return handleApiRequest<Content, "get">(url, "get");
};

// 내 모델 리스트를 가져오는 함수
export const fetchMyModels = async (
  params: ModelQueryParams = DEFAULT_PARAMS,
) => {
  const queryParams = new URLSearchParams();

  // 기본값과 사용자 지정 매개변수 병합
  const finalParams = { ...DEFAULT_PARAMS, ...params };

  // 쿼리 파라미터 생성
  Object.entries(finalParams).forEach(([key, value]) => {
    if (value !== undefined) {
      queryParams.append(key, value.toString());
    }
  });

  // 최종 URL 생성
  const url = `/api/v1/models/users?${queryParams.toString()}`;
  console.log("API 요청 URL:", url);
  return handleApiRequest<Content, "get">(url, "get");
};

// 모델 삭제하는 함수
export const deleteModel = async (modelId: number) => {
  const url = `/api/v1/models/${modelId}`;
  return handleApiRequest<void, "delete">(url, "delete");
};

// 내 작업중인 모델 리스트를 가져오는 함수
export const fetchMyWorkingModels = async (
  params: ModelQueryParams = DEFAULT_PARAMS,
) => {
  const queryParams = new URLSearchParams();

  // 기본값과 사용자 지정 매개변수 병합
  const finalParams = { ...DEFAULT_PARAMS, ...params };

  // 쿼리 파라미터 생성
  Object.entries(finalParams).forEach(([key, value]) => {
    if (value !== undefined) {
      queryParams.append(key, value.toString());
    }
  });

  // 최종 URL 생성
  const url = `/api/v1/models/versions/users/working?${queryParams.toString()}`;
  console.log("API 요청 URL:", url);
  return handleApiRequest<MyModelList, "get">(url, "get");
};

// 모델 버전 삭제하는 함수
export const deleteVersion = async (versionId: number) => {
  const url = `/api/v1/models/versions/${versionId}`;
  return handleApiRequest<void, "delete">(url, "delete");
};

// 모델의 버전들 조회
export const fetchModelVersions = async (modelId: number) => {
  const url = `/api/v1/models/${modelId}`;
  console.log("API 요청 URL:", url);
  return handleApiRequest<ModelVersions, "get">(url, "get");
};

// 모델 버전 상세 조회
export const fetchVersionDetails = async (versionId: number) => {
  const url = `/api/v1/models/versions/${versionId}`;
  console.log("API 요청 URL:", url);
  return handleApiRequest<VersionResponse, "get">(url, "get");
};

// 모델 최초 생성
export const createModel = async (modelData: ModelRequest) => {
  const url = `/api/v1/models`;
  console.log("API 요청 URL:", url);
  return handleApiRequest<ModelResponse, "post", ModelRequest>(
    url,
    "post",
    modelData,
  );
};

// 모델 버전 저장 함수
export const saveModelVersion = async (
  versionId: number,
  versionData: ModelVersionRequest,
) => {
  const url = `/api/v1/models/versions/${versionId}`;
  console.log("API 요청 URL:", url);

  // PATCH 요청을 통해 모델 버전 업데이트
  return handleApiRequest<void, "patch", ModelVersionRequest>(
    url,
    "patch",
    versionData,
  );
};

// 모델 실행
export const runModelVersion = async (versionId: number) => {
  const url = `/api/v1/models/versions/${versionId}/result/run`;
  console.log("API 요청 URL:", url);

  return handleApiRequest<RunResponse, "post", { versionId: number }>(
    url,
    "post",
    { versionId },
  );
};

// 실행 후 결과 저장
export const saveResult = async (versionId: number) => {
  const url = `/api/v1/models/versions/${versionId}/result/save`;
  console.log("API 요청 URL:", url);

  return handleApiRequest<RunResponse, "post", { versionId: number }>(
    url,
    "post",
    { versionId },
  );
};

// 모델 버전 생성 함수
export const createVersion = async (
  modelId: number,
  modelVersionId: number,
) => {
  // URL에 modelId는 경로로, modelVersionId는 쿼리 매개변수로 추가
  const url = `/api/v1/models/versions/${modelId}?modelVersionId=${modelVersionId}`;
  console.log("API 요청 URL:", url);

  // 요청 본문 데이터가 필요 없다면 생략 가능
  return handleApiRequest<ModelResponse, "post">(url, "post");
};
