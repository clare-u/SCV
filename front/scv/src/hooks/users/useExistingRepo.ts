import { useMutation } from "@tanstack/react-query";
import { existingRepo } from "@/libs";
import { toast } from "sonner";
import { useFetchUser } from "./useFetchUser";
import { CreateRepo, ApiErrorResponse } from "@/types";

export const useUpdateRepo = () => {
  const { refetch } = useFetchUser();
  return useMutation({
    mutationFn: existingRepo,

    onSuccess: (data: CreateRepo) => {
      toast.success(`기존 "${data.repoName}" 레포지토리에 연동되었습니다`);
      refetch();
    },

    onError: (error: ApiErrorResponse) => {
      // 에러 발생 시 사용자에게 알림
      toast.error(error.message);
    },
  });
};
