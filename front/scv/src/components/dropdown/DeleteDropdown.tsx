"use client";
import { useState } from "react";
import { useDeleteModel } from "@/hooks";
import { toast } from "sonner";

interface DeleteDropdownProps {
  modelId: number;
  onClick?: (event: React.MouseEvent) => void;
}

const DeleteDropdown: React.FC<DeleteDropdownProps> = ({
  modelId,
  onClick,
}) => {
  const [isDropboxOpen, setIsDropboxOpen] = useState(false);
  const { mutate: deleteModelMutation, isPending } = useDeleteModel();

  const toggleDropbox = (event: React.MouseEvent) => {
    event.stopPropagation();
    setIsDropboxOpen(!isDropboxOpen);
  };

  const handleDeleteClick = (event: React.MouseEvent) => {
    event.stopPropagation();
    const confirmDelete = confirm(
      "해당 모델과 하위 버전 전체를 삭제하시겠습니까?",
    );
    if (confirmDelete) {
      deleteModelMutation(modelId, {
        onSuccess: () => {
          toast.success("모델이 성공적으로 삭제되었습니다.");
          setIsDropboxOpen(false);
        },
        onError: (error) => {
          toast.error("모델 삭제 중 오류가 발생했습니다.");
          console.error("Delete error:", error);
        },
      });
    }
  };

  return (
    <div className="relative z-10 flex flex-col" onClick={onClick}>
      <span
        className="material-symbols-outlined cursor-pointer"
        onClick={toggleDropbox}
      >
        more_vert
      </span>
      {isDropboxOpen && (
        <div className="absolute right-0 top-full z-20">
          <div className="inline-flex flex-col gap-4 rounded-4 bg-white p-6 shadow-s">
            <div
              onClick={handleDeleteClick}
              className={`flex cursor-pointer items-center gap-10 whitespace-nowrap rounded-4 bg-white px-16 py-10 text-14 hover:bg-gray-100 ${
                isPending ? "opacity-50" : ""
              }`}
            >
              삭제하기
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DeleteDropdown;
