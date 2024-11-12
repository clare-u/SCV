import { useQuery } from "@tanstack/react-query";
import { fetchModelVersions } from "@/libs";
import { ModelVersions } from "@/types";

// 모델의 버전들 조회
export const useFetchModelVersions = (modelId: number) => {
  return useQuery<ModelVersions, Error>({
    queryKey: ["modelVersions", modelId],
    queryFn: () => fetchModelVersions(modelId),
  });
};
