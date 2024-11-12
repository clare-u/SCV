import { useMutation } from "@tanstack/react-query";
import { exportModel } from "@/libs";
import { ExportRequest } from "@/types";
import { toast } from "sonner";

export const useExportModel = () => {
  return useMutation({
    mutationFn: (data: ExportRequest) => exportModel(data),
    onSuccess: () => {
      toast.success("내보내기 완료되었습니다.");
    },
    onError: (error) => {
      toast.error("내보내기 중 오류가 발생했습니다.");
      console.error("Export failed:", error);
    },
  });
};
