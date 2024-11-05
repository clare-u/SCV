"use client";

import { Suspense, useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Button from "@/components/button/Button";
import Badge from "@/components/badge/Badge";
import Chips from "@/components/chips/Chips";
import SearchInput from "@/components/input/SearchInput";
import ModalInput from "@/components/input/ModalInput";
import ListboxComponent from "@/components/input/ListBoxComponent";
import NewModal from "@/components/modal/NewModal";
import CloneModal from "@/components/modal/CloneModal";
import BoardCard from "@/components/card/BoardCard";
import WorkspaceCard from "@/components/card/WorkspaceCard";
import Pagination from "@/components/pagination/Pagination";
import ModalButton from "@/components/button/ModalButton";

function Home() {
  // 검색 인풋
  const router = useRouter();

  const handleSearchSubmit = (value: string) => {
    router.push(`/?keyword=${value}`);
  };

  // 모달 인풋
  const [inputValue, setInputValue] = useState("");

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const [input2Value, setInput2Value] = useState("");

  const handleInput2Change = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInput2Value(e.target.value);
  };

  // 리스트박스
  const option = [
    { id: 1, name: "새 레포지토리와 연동" },
    { id: 2, name: "기존 레포지토리와 연동" },
  ];

  const version = [
    { id: 1, name: "v1" },
    { id: 2, name: "v2" },
    { id: 3, name: "v3" },
    { id: 4, name: "v4" },
    { id: 5, name: "v5" },
    { id: 6, name: "v6" },
  ];

  const [selectedOption, setSelectedOption] = useState(option[0]);

  const [selectedVersion, setSelectedVersion] = useState(version[0]);

  // board카드
  const datasets = ["MNIST", "Fashion", "CIFAR-10", "SVHN", "EMNIST"];

  // 더미 데이터 생성을 위한 헬퍼 함수
  const cards = datasets.map((dataset, index) => ({
    modelId: `model-${index + 1}`,
    versionId: `version-${index + 1}`,
    title: `Model ${index + 1}`,
    version: `v${index + 1}`,
    dataset: dataset,
    profileImg: `/profile.png`,
    nickname: `User${index + 1}`,
    accuracy: parseFloat((85 + index * 2).toFixed(2)), // float 형식으로 소수점 2자리까지
    updatedAt: new Date(Date.now() - index * 100000000).toISOString(),
  }));

  // workspace카드
  const workspaceDatasets = [
    "MNIST",
    "Fashion",
    "CIFAR-10",
    "SVHN",
    "EMNIST",
    "Editing",
  ];

  // 더미 데이터 생성
  const workspaceCards = workspaceDatasets.map((dataset, i) => ({
    modelId: `model-${i + 1}`,
    versionId: `v-${i + 1}`,
    title: `Model ${i + 1}`,
    version: `v${i + 1}`,
    dataset: dataset,
    accuracy: parseFloat((85 + i * 1.5).toFixed(2)), // 정확도 값
    createdAt: new Date(Date.now() - i * 10000000).toISOString(), // 예시 생성일
    updatedAt: new Date(Date.now() - i * 5000000).toISOString(), // 예시 수정일
  }));

  // 페이지네이션
  const [totalItems, setTotalItems] = useState(0);
  const searchParams = useSearchParams();
  const page = searchParams.get("page");

  useEffect(() => {
    window.scrollTo(0, 0); // 페이지 이동 시 스크롤 위치 맨 위로 초기화
    /* api 호출 및 데이터(totalItems, books) 저장 */
  }, [page]);

  return (
    <div className="flex flex-col gap-10 p-20">
      {/* Indigo fill 버튼 */}
      <div className="flex gap-10">
        <Button size="l" design="fill" color="indigo" icon="add_box">
          Indigo Fill Large
        </Button>
        <Button size="m" design="fill" color="indigo" icon="add_box">
          Indigo Fill Medium
        </Button>
        <Button size="s" design="fill" color="indigo" icon="add_box">
          Indigo Fill Small
        </Button>
      </div>

      {/* Green fill and outline 버튼 */}
      <div className="flex gap-10">
        <Button size="m" design="fill" color="green" icon="add_box">
          Green Fill
        </Button>
        <Button size="m" design="outline" color="green" icon="add_box">
          Green Outline
        </Button>
      </div>

      {/* Disabled Button */}
      <div className="flex gap-10">
        <Button size="m" design="fill" color="red" disabled icon="add_box">
          Disabled Button
        </Button>
      </div>

      {/* Badge */}
      <div className="flex gap-10">
        <Badge color="red">Badge</Badge>
        <Badge color="gray">Badge</Badge>
        <Badge color="green">Badge</Badge>
        <Badge color="blue">Badge</Badge>
        <Badge color="amber">Badge</Badge>
        <Badge color="teal">Badge</Badge>
      </div>

      {/* Chips */}
      <div className="flex gap-10">
        <Chips color="indigo" design="fill">
          Chips
        </Chips>
        <Chips color="teal" design="fill">
          Chips
        </Chips>
        <Chips color="gray" design="fill">
          Chips
        </Chips>
        <Chips color="amber" design="fill">
          Chips
        </Chips>
        <Chips color="red" design="fill">
          Chips
        </Chips>
        <Chips color="green" design="fill">
          Chips
        </Chips>
        <Chips color="black" design="fill">
          Chips
        </Chips>
      </div>

      <div className="flex gap-10">
        <Chips color="indigo" design="outline">
          Chips
        </Chips>
        <Chips color="teal" design="outline">
          Chips
        </Chips>
        <Chips color="gray" design="outline">
          Chips
        </Chips>
        <Chips color="amber" design="outline">
          Chips
        </Chips>
        <Chips color="red" design="outline">
          Chips
        </Chips>
        <Chips color="green" design="outline">
          Chips
        </Chips>
        <Chips color="black" design="outline">
          Chips
        </Chips>
      </div>

      {/* Input */}
      <div className="flex flex-col gap-10">
        {/* <SearchInput placeholder="placeholder" onSubmit={handleSearchSubmit} /> */}
        <ModalInput
          placeholder="placeholder"
          value={inputValue}
          onChange={handleInputChange}
        />
        <ModalInput
          placeholder="placeholder"
          value={input2Value}
          onChange={handleInput2Change}
          color="dark"
        />
        <ListboxComponent
          value={selectedOption}
          onChange={setSelectedOption}
          options={option}
          color="dark"
        />
        <ListboxComponent
          value={selectedVersion}
          onChange={setSelectedVersion}
          options={version}
        />
      </div>

      {/* Modal */}
      <div className="flex gap-10">
        <NewModal />
        <CloneModal />
      </div>

      {/* boardCard */}
      {/* <div className="grid w-[1100px] grid-cols-3 gap-10">
        {cards.map((card) => (
          <BoardCard key={card.modelId} {...card} />
        ))}
      </div> */}

      {/* workspaceCard */}
      <div className="grid w-[1100px] grid-cols-3 gap-10">
        {workspaceCards.map((card) => (
          <WorkspaceCard key={card.modelId} {...card} />
        ))}
      </div>

      {/* 페이지네이션 */}
      <Pagination
        totalItems={10000}
        currentPage={page && parseInt(page) > 0 ? parseInt(page) : 1}
        pageCount={10}
        itemCountPerPage={50}
      />

      {/* modalButton */}
      <div className="flex bg-indigo-900 p-20">
        <ModalButton icon="logout" disabled>
          Button
        </ModalButton>
      </div>
    </div>
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <Home />
    </Suspense>
  );
}
