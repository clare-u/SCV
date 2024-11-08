import { useMutation } from "@tanstack/react-query";
import { useFetchUser } from "./useFetchUser";
import { ApiErrorResponse } from "@/types";
import { deleteRepo } from "@/libs";
import { toast } from "sonner";

export const useDeleteRepo = () => {
  const { refetch } = useFetchUser();
  return useMutation({
    mutationFn: deleteRepo,

    onSuccess: () => {
      // 성공 시 토스트 메시지 표시
      toast.success("레포지토리 연동이 해제되었습니다.");
      refetch();
    },

    onError: (error: ApiErrorResponse) => {
      // 에러 시 토스트 메시지 표시
      toast.error(error.message);
    },
  });
};
