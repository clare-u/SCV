import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteModel } from "@/libs/api/models";

const DEFAULT_QUERY_KEY = "models";

export const useDeleteModel = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteModel,
    onSuccess: () => {
      // 모델 삭제 후 모델 리스트 캐시 무효화
      queryClient.invalidateQueries({
        queryKey: [DEFAULT_QUERY_KEY],
      });
    },
  });
};
