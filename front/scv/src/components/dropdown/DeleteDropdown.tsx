"use client";
import { useState } from "react";
import { useDeleteModel, useDeleteVersion } from "@/hooks";

interface DeleteDropdownProps {
  modelId?: number;
  versionId?: number;
  onClick?: (event: React.MouseEvent) => void;
}

const DeleteDropdown: React.FC<DeleteDropdownProps> = ({
  modelId,
  versionId,
  onClick,
}) => {
  const [isDropboxOpen, setIsDropboxOpen] = useState(false);
  const { mutate: deleteModelMutation, isPending: isDeletingModel } =
    useDeleteModel();
  const { mutate: deleteVersionMutation, isPending: isDeletingVersion } =
    useDeleteVersion();

  const toggleDropbox = (event: React.MouseEvent) => {
    event.stopPropagation();
    setIsDropboxOpen(!isDropboxOpen);
  };

  const handleDeleteClick = (event: React.MouseEvent) => {
    event.stopPropagation();

    if (modelId) {
      // 모델 삭제 로직
      const confirmDelete = confirm(
        "해당 모델과 하위 버전 전체를 삭제하시겠습니까?",
      );
      if (confirmDelete) {
        deleteModelMutation(modelId, {
          onSuccess: () => {
            setIsDropboxOpen(false);
          },
        });
      }
    } else if (versionId) {
      // 버전 삭제 로직
      const confirmDelete = confirm("해당 버전을 삭제하시겠습니까?");
      if (confirmDelete) {
        deleteVersionMutation(versionId, {
          onSuccess: () => {
            setIsDropboxOpen(false);
          },
        });
      }
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
                isDeletingModel || isDeletingVersion ? "opacity-50" : ""
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
