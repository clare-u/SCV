"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import FilterDropdown from "@/components/input/FilterDropdown";
import BoardCard from "@/components/card/BoardCard";
import Pagination from "@/components/pagination/Pagination";
import DatasetRadio from "@/components/input/DatasetRadio";
import SearchInput from "@/components/input/SearchInput";

export default function Community() {
  // 필터 라디오
  const dataset = ["전체", "MNIST", "Fashion", "CIFAR-10", "SVHN", "EMNIST"];
  const [selected, setSelected] = useState(dataset[0]);

  // 정렬 필터
  const [selectedFilter, setSelectedFilter] = useState("추천순");
  const filterOptions = ["추천순", "최신순", "오래된순"]; // 필터 옵션 리스트

  const handleFilterChange = (filter: string) => {
    setSelectedFilter(filter);
    // 필터 변경에 따른 추가 로직을 여기에 추가
  };

  // 검색 인풋
  const router = useRouter();

  const handleSearchSubmit = (value: string) => {
    router.push(`/community?keyword=${value}`);
  };

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

  // 페이지네이션
  const [totalItems, setTotalItems] = useState(1000);
  const searchParams = useSearchParams();
  const page = searchParams.get("page");

  useEffect(() => {
    window.scrollTo(0, 0); // 페이지 이동 시 스크롤 위치 맨 위로 초기화
    /* api 호출 및 데이터(totalItems, books) 저장 */
  }, [page]);

  return (
    <div className="flex flex-col gap-10 py-10">
      {/* 필터, 검색 버튼 */}
      <div className="flex items-center justify-between">
        <FilterDropdown
          selectedFilter={selectedFilter}
          onSelectFilter={handleFilterChange}
          filterOptions={filterOptions}
        />
        <DatasetRadio
          options={dataset}
          selected={selected}
          onChange={setSelected}
        />
        <div className="w-[400px]">
          <SearchInput
            placeholder="placeholder"
            onSubmit={handleSearchSubmit}
          />
        </div>
      </div>
      {/* boardCard */}
      <div className="grid grid-cols-3 gap-10 px-10 py-20">
        {cards.map((card) => (
          <BoardCard key={card.modelId} {...card} />
        ))}
      </div>
      {/* 페이지네이션 */}
      <Pagination
        totalItems={totalItems}
        currentPage={page && parseInt(page) > 0 ? parseInt(page) : 1}
        pageCount={10}
        itemCountPerPage={50}
      />
    </div>
  );
}
