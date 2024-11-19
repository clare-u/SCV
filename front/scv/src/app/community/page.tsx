"use client";

import { Suspense, useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import FilterDropdown from "@/components/input/FilterDropdown";
import BoardCard from "@/components/card/BoardCard";
import Pagination from "@/components/pagination/Pagination";
import DatasetRadio from "@/components/input/DatasetRadio";
import SearchInput from "@/components/input/SearchInput";
import Loading from "@/components/loading/Loading";
import { useFetchModels } from "@/hooks";
import { ModelQueryParams } from "@/types";

function Community() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 검색 인풋
  const currentKeyword = searchParams.get("modelName") || "";
  const [searchValue, setSearchValue] = useState(currentKeyword);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchValue(e.target.value);
  };

  // URL 파라미터 가져오기
  const currentPage = searchParams.get("page")
    ? parseInt(searchParams.get("page")!)
    : 1;
  const currentDataName = searchParams.get("dataName") || "전체";
  const currentOrder = searchParams.get("order") || "추천순";

  // 상태 관리
  const dataName = ["전체", "MNIST", "Fashion", "CIFAR10", "SVHN", "EMNIST"];
  const [selected, setSelected] = useState(currentDataName);
  const [selectedFilter, setSelectedFilter] = useState(currentOrder);
  const filterOptions = ["추천순", "최신순", "오래된순"];

  // Query 파라미터 변환 함수
  const getSortParams = (
    filter: string,
  ): Partial<Pick<ModelQueryParams, "orderBy" | "direction">> => {
    switch (filter) {
      case "최신순":
        return { orderBy: "updatedAt", direction: "desc" };
      case "오래된순":
        return { orderBy: "updatedAt", direction: "asc" };
      case "추천순":
      default:
        return {};
    }
  };

  // 모델 데이터 fetch
  const { data, isLoading, error } = useFetchModels({
    page: currentPage - 1,
    size: 12,
    ...getSortParams(selectedFilter),
    modelName: currentKeyword || undefined, // 검색어가 없을 때는 undefined
    dataName: selected !== "전체" ? selected : undefined,
  });

  // URL 업데이트 함수
  const updateURL = (params: {
    [key: string]: string | number | undefined;
  }) => {
    const current = new URLSearchParams(Array.from(searchParams.entries()));

    Object.entries(params).forEach(([key, value]) => {
      if (value) {
        current.set(key, String(value));
      } else {
        current.delete(key);
      }
    });

    // 검색어가 빈 문자열이면 keyword 파라미터 삭제
    if (params.modelName === "") {
      current.delete("modelName");
    }

    router.push(`/community?${current.toString()}`);
  };

  // 이벤트 핸들러
  const handleFilterChange = (filter: string) => {
    setSelectedFilter(filter);
    updateURL({ order: filter, page: 1 });
  };

  const handleDataNameChange = (dataName: string) => {
    setSelected(dataName);
    updateURL({ dataName, page: 1 });
  };

  const handleSearchSubmit = (value: string) => {
    // 검색어가 빈 문자열이면 keyword 파라미터 삭제
    updateURL({
      modelName: value || undefined,
      page: 1,
    });
  };

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [currentPage]);

  // data 로드 시 콘솔에 출력
  if (data) {
    // 데이터가 로드되었을 때만 출력
  }

  if (isLoading) return <Loading />;
  if (error) return <div>에러 발생: {error.message}</div>;

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
          options={dataName}
          selected={selected}
          onChange={handleDataNameChange}
        />
        <div className="w-[400px]">
          <SearchInput
            placeholder="모델명을 검색하세요."
            value={searchValue}
            onChange={handleSearchChange}
            onSubmit={handleSearchSubmit}
          />
        </div>
      </div>

      {/* boardCard */}
      <div className="grid grid-cols-3 gap-10 px-10 py-20">
        {data?.content.length === 0 ? (
          <div>모델이 없습니다.</div> // 데이터가 없을 경우 메시지 출력
        ) : (
          data?.content.map((model) => (
            <BoardCard
              key={model.modelId}
              modelId={model.modelId}
              versionId={model.latestVersionId}
              title={model.modelName}
              version={`v${model.latestVersion}`} // version 값 수정
              dataset={model.dataName}
              profileImg={model.userProfile.userImageUrl || "/profile.png"}
              nickname={model.userProfile.userNickname}
              accuracy={model.accuracy}
              updatedAt={model.updatedAt}
            />
          ))
        )}
      </div>

      {/* 페이지네이션 */}
      <Pagination
        totalItems={data!.totalElements}
        currentPage={currentPage}
        pageCount={10}
        itemCountPerPage={12}
      />
    </div>
  );
}

export default function Page() {
  return (
    <Suspense fallback={<Loading />}>
      <Community />
    </Suspense>
  );
}
