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
  const url = `/models?${queryParams.toString()}`;
  return handleApiRequest<Content, "get">(url, "get");
};
