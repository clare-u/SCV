// hooks/useFetchModels.ts
import { useQuery } from "@tanstack/react-query";
import { fetchMyModels } from "@/libs";
import { ModelQueryParams } from "@/types";

// 기본 쿼리 키 상수
const DEFAULT_QUERY_KEY = "models";

// 모델 리스트를 가져오는 커스텀 훅
export const useFetchMyModels = (
  params: ModelQueryParams = { page: 1, size: 12 },
) => {
  return useQuery({
    queryKey: [DEFAULT_QUERY_KEY, params],
    queryFn: () => fetchMyModels(params),
  });
};
