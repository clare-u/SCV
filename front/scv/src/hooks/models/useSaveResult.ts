import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { saveResult } from "@/libs";
import { RunResponse } from "@/types";

export const useSaveResult = () => {
  return useMutation<RunResponse, Error, number>({
    mutationFn: (versionId) => saveResult(versionId),
    onSuccess: (data) => {
      // 필요한 경우 실행 결과를 처리
    },
    onError: (error) => {
      toast.error("결과 저장에 실패했습니다.");
      console.error("모델 실행 오류:", error);
    },
  });
};
