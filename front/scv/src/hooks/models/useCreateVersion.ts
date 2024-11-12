import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { ModelResponse } from "@/types";
import { createVersion } from "@/libs";

// 모델 버전 생성 훅 정의
export const useCreateVersion = () => {
  return useMutation<
    ModelResponse, // 성공 시 응답 타입
    Error, // 에러 타입
    ModelResponse // mutation 함수에 전달될 파라미터 타입
  >({
    mutationFn: ({ modelId, modelVersionId }) =>
      createVersion(modelId, modelVersionId),

    onSuccess: (data) => {
      // 성공 시 추가적인 작업이 필요하다면 여기에 작성
      toast.success("새로운 버전이 성공적으로 생성되었습니다.");
    },

    onError: (error) => {
      toast.error("모델 버전 생성에 실패했습니다.");
      console.log("버전 생성 에러: ", error);
    },
  });
};
