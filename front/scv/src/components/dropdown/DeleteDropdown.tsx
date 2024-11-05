"use client";
import { useState } from "react";

interface DeleteDropdownProps {
  onClick?: (event: React.MouseEvent) => void;
}

const DeleteDropdown: React.FC<DeleteDropdownProps> = ({ onClick }) => {
  const [isDropboxOpen, setIsDropboxOpen] = useState(false); // Dropbox 열림 상태 관리

  const toggleDropbox = (event: React.MouseEvent) => {
    event.stopPropagation();
    setIsDropboxOpen(!isDropboxOpen); // 토글 기능 구현
  };

  const handleDeleteClick = (event: React.MouseEvent) => {
    event.stopPropagation();
    const confirmDelete = confirm(
      "해당 모델과 하위 버전 전체를 삭제하시겠습니까?",
    );
    if (confirmDelete) {
      // 삭제 로직을 여기에 추가
      console.log("삭제가 진행되었습니다.");
    }
  };

  return (
    <div className="relative z-10 flex flex-col" onClick={onClick}>
      <span
        className="material-symbols-outlined cursor-pointer"
        onClick={toggleDropbox}
      >
        more_horiz
      </span>
      {isDropboxOpen && (
        <div className="absolute right-0 top-full z-20">
          <div className="inline-flex flex-col gap-4 rounded-4 bg-white p-6 shadow-s">
            <div
              onClick={handleDeleteClick}
              className="flex cursor-pointer items-center gap-10 whitespace-nowrap rounded-4 bg-white px-16 py-10 text-14 hover:bg-gray-100"
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
