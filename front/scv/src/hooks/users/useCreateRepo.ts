import { useMutation } from "@tanstack/react-query";
import { createRepo } from "@/libs";
import { useFetchUser } from "./useFetchUser";
import { CreateRepo, ApiErrorResponse } from "@/types";
import { toast } from "sonner";

export const useCreateRepo = () => {
  const { refetch } = useFetchUser();
  return useMutation({
    mutationFn: createRepo,

    // TODO: 관련 캐시 무효화 처리?
    onSuccess: (data: CreateRepo) => {
      toast.success(
        `새로 생성된 "${data.repoName}" 레포지토리에 연동되었습니다`,
      );
      refetch();
    },

    // 에러 처리
    onError: (error: ApiErrorResponse) => {
      toast.error(error.message);
    },
  });
};
