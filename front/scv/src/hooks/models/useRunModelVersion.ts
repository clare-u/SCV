import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { runModelVersion } from "@/libs";
import { RunResponse } from "@/types";

export const useRunModelVersion = () => {
  return useMutation<RunResponse, Error, number>({
    mutationFn: (versionId) => runModelVersion(versionId),
    onSuccess: (data) => {
      toast.success("모델이 성공적으로 실행되었습니다.");
      console.log("실행 결과:", data); // 필요한 경우 실행 결과를 처리
    },
    onError: (error) => {
      toast.error("모델 실행에 실패했습니다.");
      console.error("모델 실행 오류:", error);
    },
  });
};
