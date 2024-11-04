// hooks/useFetchModels.ts
import { useQuery } from "@tanstack/react-query";
import { fetchModels } from "@/libs/api/models";
import { ModelQueryParams } from "@/types";

// 기본 쿼리 키 상수
const DEFAULT_QUERY_KEY = "models";

// 모델 리스트를 가져오는 커스텀 훅
export const useFetchModels = (
  params: ModelQueryParams = { page: 1, size: 12 },
) => {
  return useQuery({
    queryKey: [DEFAULT_QUERY_KEY, params],
    queryFn: () => fetchModels(params),
  });
};
