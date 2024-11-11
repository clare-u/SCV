import { useQuery } from "@tanstack/react-query";
import { fetchVersionDetails } from "@/libs";
import { VersionResponse } from "@/types";

// 모델 버전 상세 조회
export const useFetchVersionDetails = (versionId: number) => {
  return useQuery<VersionResponse, Error>({
    queryKey: ["VersionDetails", versionId],
    queryFn: () => fetchVersionDetails(versionId),
    retry: 1,
    retryDelay: 10,
  });
};
