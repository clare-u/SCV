import { Content, ModelQueryParams } from "@/types";
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
