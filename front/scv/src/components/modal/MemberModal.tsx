"use client";

import { useState } from "react";
import Image from "next/image";
import ModalInput from "../input/ModalInput";
import ModalButton from "../button/ModalButton";
import ListboxComponent from "../input/ListBoxComponent";
import { useLogOut, useCreateRepo, useUpdateRepo } from "@/hooks";
import Loading from "../loading/Loading";
import { toast } from "sonner";

interface MemberModalProps {
  image: string;
  nickname: string;
  email: string;
  repo?: string;
}

const MemberModal: React.FC<MemberModalProps> = ({
  image,
  nickname,
  email,
  repo,
}) => {
  // 로그아웃
  const { mutate: handleLogout, isPending } = useLogOut();

  const [isFormOpen, setIsFormOpen] = useState(false); // 폼 열림 여부 상태

  // 폼 열기/닫기 토글 함수
  const toggleForm = () => {
    setIsFormOpen((prev) => !prev);
  };

  // 리스트박스
  const option = [
    { id: 1, name: "새 레포지토리와 연동" },
    { id: 2, name: "기존 레포지토리와 연동" },
  ];

  const [selectedOption, setSelectedOption] = useState(option[0]);

  // 모달 인풋
  const [inputValue, setInputValue] = useState("");

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  // 연동하기
  const { mutate: createRepo, isPending: isRepoPending } = useCreateRepo();
  const { mutate: updateRepo, isPending: isUpdatePending } = useUpdateRepo();
  const handleRepoSubmit = () => {
    if (!inputValue.trim()) {
      toast.error("레포지토리 이름을 입력해주세요.");
      return;
    }

    // 선택된 옵션에 따라 다른 API 호출
    if (selectedOption.id === 1) {
      // 새 레포지토리 생성
      createRepo({ repoName: inputValue });
    } else {
      // 기존 레포지토리 연동
      updateRepo({ repoName: inputValue });
    }
  };

  // repo URL의 마지막 부분만 추출
  const formattedRepo = repo ? repo.replace("https://github.com/", "") : "";

  // a 태그 href
  const githubUrl = `https://github.com/${nickname}/${repo?.replace(" ", "-")}`;

  return (
    <div className="absolute right-0 z-50 mt-2 flex flex-col items-center justify-center gap-10 rounded-10 border border-gray-400 bg-indigo-800 p-20 text-white shadow-lg">
      <div className="flex flex-col items-center justify-center gap-10 p-10">
        <Image
          src={image ? `${image}` : "/profile.png"}
          alt={nickname}
          width={100}
          height={100}
          className="rounded-full"
        />
        <div className="whitespace-nowrap text-20 font-600">{nickname}</div>
        <div className="whitespace-nowrap text-16">{email}</div>
      </div>
      {/* 구분선 */}
      <div className="h-1 self-stretch border border-gray-400"></div>
      {/* repo가 존재할 경우 링크 표시, 없을 경우 "연동하기" 버튼 */}
      {repo ? (
        <a
          href={githubUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="flex gap-[5px] whitespace-nowrap text-white underline"
        >
          <Image
            src="/github-mark-white.png"
            alt="github-mark-white"
            width={24}
            height={24}
          ></Image>
          {formattedRepo} {/* 추출된 repo 경로만 표시 */}
        </a>
      ) : (
        <>
          <div className="flex flex-col gap-10 p-10">
            <button
              onClick={toggleForm}
              className="inline-flex min-w-[200px] shrink-0 items-center justify-center gap-[5px] whitespace-nowrap py-10 text-16 font-medium text-white hover:cursor-pointer hover:font-bold"
            >
              <Image
                src="/github-mark-white.png"
                alt="github-mark-white"
                width={24}
                height={24}
              ></Image>
              레포지토리 연동
            </button>

            {/* 폼이 열려 있을 때만 표시 */}
            {isFormOpen && (
              <>
                <ListboxComponent
                  value={selectedOption}
                  onChange={setSelectedOption}
                  options={option}
                  color="dark"
                />
                <ModalInput
                  placeholder="Repository Name"
                  value={inputValue}
                  onChange={handleInputChange}
                  color="dark"
                />
                <ModalButton
                  icon="add_link"
                  onClick={handleRepoSubmit}
                  disabled={isRepoPending || isUpdatePending}
                >
                  연동하기
                </ModalButton>
              </>
            )}
          </div>
        </>
      )}
      {/* 구분선 */}
      <div className="h-1 self-stretch border border-gray-400"></div>

      {isPending ? (
        <Loading />
      ) : (
        <ModalButton icon="logout" onClick={handleLogout}>
          로그아웃
        </ModalButton>
      )}
    </div>
  );
};

export default MemberModal;
