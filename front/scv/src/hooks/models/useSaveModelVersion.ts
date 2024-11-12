import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";
import { ModelVersionRequest } from "@/types";
import { saveModelVersion } from "@/libs";

interface SaveModelVersionParams {
  versionId: number;
  versionData: ModelVersionRequest;
}

export const useSaveModelVersion = () => {
  return useMutation({
    mutationFn: ({ versionId, versionData }: SaveModelVersionParams) =>
      saveModelVersion(versionId, versionData),

    onSuccess: () => {
      toast.success("버전이 성공적으로 저장되었습니다.");
    },

    onError: (error) => {
      console.error("모델 버전 저장 오류:", error);
      toast.error("모델 버전 저장에 실패했습니다.");
    },
  });
};
