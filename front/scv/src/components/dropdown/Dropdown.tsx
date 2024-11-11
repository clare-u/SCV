"use client";
import { useState } from "react";
import { useDeleteModel, useDeleteVersion } from "@/hooks";
import { toast } from "sonner";
import { useRouter } from "next/navigation";

interface DropdownProps {
  modelId: number;
  versionId: number;
}

const Dropdown: React.FC<DropdownProps> = ({ modelId, versionId }) => {
  const router = useRouter();

  const [isDropboxOpen, setIsDropboxOpen] = useState(false);
  const { mutate: deleteModelMutation, isPending: isModelPending } =
    useDeleteModel();
  const { mutate: deleteVersionMutation, isPending: isVersionPending } =
    useDeleteVersion();

  const toggleDropbox = () => {
    setIsDropboxOpen(!isDropboxOpen);
  };

  // 편집하기
  const handleEditClick = () => {
    router.push(`/edit/${modelId}/${versionId}`);
  };

  // 새로운 버전 만들기
  const handleCreateVersionClick = () => {};

  // 버전 삭제하기
  const handleVersionDeleteClick = () => {
    const confirmVersionDelete = confirm("현재 버전을 삭제하시겠습니까?");
    if (confirmVersionDelete) {
      deleteVersionMutation(versionId, {
        onSuccess: () => {
          toast.success("버전이 성공적으로 삭제되었습니다.");
          setIsDropboxOpen(false);
        },
        onError: (error) => {
          toast.error("버전 삭제 중 오류가 발생했습니다.");
          console.error("Delete error:", error);
        },
      });
    }
  };

  // 모델 삭제하기
  const handleDeleteClick = () => {
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
    <div className="relative z-10 flex flex-col">
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
              onClick={handleEditClick}
              className={`flex cursor-pointer items-center gap-10 whitespace-nowrap rounded-4 bg-white px-16 py-10 text-14 hover:bg-gray-100`}
              // TODO: isPending 말고 다른 변수 할당 필요
            >
              <span className="material-symbols-outlined">edit</span>
              편집하기
            </div>
            <div
              onClick={handleCreateVersionClick}
              className={`flex cursor-pointer items-center gap-10 whitespace-nowrap rounded-4 bg-white px-16 py-10 text-14 hover:bg-gray-100`}
            >
              <span className="material-symbols-outlined">add_circle</span>
              새로운 버전 만들기
            </div>
            <div
              onClick={handleVersionDeleteClick}
              className={`flex cursor-pointer items-center gap-10 whitespace-nowrap rounded-4 bg-white px-16 py-10 text-14 hover:bg-gray-100 ${
                isVersionPending ? "opacity-50" : ""
              }`}
            >
              <span className="material-symbols-outlined">delete</span>
              버전 삭제하기
            </div>
            <div
              onClick={handleDeleteClick}
              className={`flex cursor-pointer items-center gap-10 whitespace-nowrap rounded-4 bg-white px-16 py-10 text-14 hover:bg-gray-100 ${
                isModelPending ? "opacity-50" : ""
              }`}
            >
              <span className="material-symbols-outlined">delete</span>
              모델 삭제하기
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dropdown;
