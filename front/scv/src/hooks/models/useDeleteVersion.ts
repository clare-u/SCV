import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteVersion } from "@/libs";
import { toast } from "sonner";

const DEFAULT_QUERY_KEY = "models";

export const useDeleteVersion = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteVersion,
    onSuccess: () => {
      // 모델 삭제 후 모델 리스트 캐시 무효화
      queryClient.invalidateQueries({
        queryKey: [DEFAULT_QUERY_KEY],
      });
      toast.success("성공적으로 삭제되었습니다.");
    },
  });
};
