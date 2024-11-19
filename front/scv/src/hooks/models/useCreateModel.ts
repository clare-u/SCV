import { useMutation } from "@tanstack/react-query";
import { ModelRequest, ModelResponse } from "@/types";
import { createModel } from "@/libs";
import { ApiErrorResponse } from "@/types";

interface UseCreateModelOptions {
  onSuccess?: (data: ModelResponse) => void;
}

export const useCreateModel = (options?: UseCreateModelOptions) => {
  return useMutation<ModelResponse, ApiErrorResponse, ModelRequest>({
    mutationFn: (modelData: ModelRequest) => createModel(modelData),
    onSuccess: (data) => {
      options?.onSuccess?.(data);
    },
    onError: (error) => {
      console.error("모델 생성 오류:", error);
    },
  });
};
