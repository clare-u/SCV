import { useMutation } from "@tanstack/react-query";
import { updateModelTitle } from "@/libs";
import { ModelTitleRequest } from "@/types";
import { toast } from "sonner";

export const useUpdateModelTitle = () => {
  return useMutation({
    mutationFn: ({
      modelId,
      titleData,
    }: {
      modelId: number;
      titleData: ModelTitleRequest;
    }) => updateModelTitle(modelId, titleData),
    onSuccess: () => toast.success("모델명이 수정되었습니다."),
    onError: () => toast.error("모델명 수정에 실패했습니다."),
  });
};
