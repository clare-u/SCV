"use client";

import { Suspense, useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import FilterDropdown from "@/components/input/FilterDropdown";
import WorkspaceCard from "@/components/card/WorkspaceCard";
import Pagination from "@/components/pagination/Pagination";
import DatasetRadio from "@/components/input/DatasetRadio";
import SearchInput from "@/components/input/SearchInput";
import Loading from "@/components/loading/Loading";
import { useFetchMyModels, useFetchMyWorkingModels } from "@/hooks";
import { ModelQueryParams } from "@/types";
import EditingCard from "@/components/card/EditingCard";

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
  const currentOrder = searchParams.get("order") || "수정일 최신순";
  const currentViewMode = searchParams.get("viewMode") || "완료목록";

  // 상태 관리
  const dataName = ["전체", "MNIST", "Fashion", "CIFAR10", "SVHN", "EMNIST"];
  const [selected, setSelected] = useState(currentDataName);
  const [selectedFilter, setSelectedFilter] = useState(currentOrder);
  const [viewMode, setViewMode] = useState(currentViewMode);
  const filterOptions = [
    "수정일 최신순",
    "수정일 오래된순",
    "생성일 최신순",
    "생성일 오래된순",
  ];

  // Query 파라미터 변환 함수를 먼저 선언
  const getSortParams = (
    filter: string,
  ): Partial<Pick<ModelQueryParams, "orderBy" | "direction">> => {
    switch (filter) {
      case "수정일 최신순":
        return { orderBy: "updatedAt", direction: "desc" };
      case "수정일 오래된순":
        return { orderBy: "updatedAt", direction: "asc" };
      case "생성일 최신순":
        return { orderBy: "createdAt", direction: "desc" };
      case "생성일 오래된순":
        return { orderBy: "createdAt", direction: "asc" };
      default:
        return {};
    }
  };

  // 그 다음 queryParams 정의
  const queryParams = {
    page: currentPage - 1,
    size: 12,
    ...getSortParams(selectedFilter),
    modelName: currentKeyword || undefined,
    dataName: selected !== "전체" ? selected : undefined,
    viewMode,
  };

  // 모델 데이터 fetch
  const { data, isLoading, error, refetch } =
    viewMode === "임시저장"
      ? useFetchMyWorkingModels(queryParams)
      : useFetchMyModels(queryParams);

  // URL 업데이트 함수
  // 1. updateURL 함수에서 refetch 제거
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

    if (params.modelName === "") {
      current.delete("modelName");
    }

    router.push(`/workspace?${current.toString()}`);
  };
  // 이벤트 핸들러
  const handleFilterChange = async (filter: string) => {
    setSelectedFilter(filter);
    await updateURL({ order: filter, page: 1 });
  };

  const handleDataNameChange = async (dataName: string) => {
    setSelected(dataName);
    await updateURL({ dataName, page: 1 });
  };

  const handleViewModeChange = async (mode: string) => {
    setViewMode(mode);
    await updateURL({ viewMode: mode, page: 1 });
  };

  const handleSearchSubmit = async (value: string) => {
    await updateURL({
      modelName: value || undefined,
      page: 1,
    });
  };

  if (data) {
    console.log(data); // 데이터가 로드되었을 때만 출력
  }

  // URL 파라미터가 변경될 때마다 데이터 리프레시
  useEffect(() => {
    refetch();
  }, [searchParams]); // refetch 제거

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [currentPage]);

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

      <div className="flex flex-col items-center justify-center px-10">
        <div className="flex w-full gap-10">
          <button
            onClick={() => handleViewModeChange("완료목록")}
            className={`flex rounded-t-10 px-40 py-20 ${
              viewMode === "완료목록" ? "bg-blue-50" : "bg-gray-50"
            }`}
          >
            완료목록
          </button>
          <button
            onClick={() => handleViewModeChange("임시저장")}
            className={`flex rounded-t-10 px-40 py-20 ${
              viewMode === "임시저장" ? "bg-yellow-50" : "bg-gray-50"
            }`}
          >
            임시저장
          </button>
        </div>

        <div
          className={`grid w-full grid-cols-3 gap-10 rounded-b-10 px-10 py-20 ${viewMode === "완료목록" ? "bg-blue-50" : "bg-yellow-50"}`}
        >
          {data?.content.length === 0 ? (
            <div>모델이 없습니다.</div>
          ) : (
            data?.content.map((model) =>
              "title" in model ? ( // model.title이 있는 경우
                <EditingCard
                  key={model.modelVersionId}
                  modelId={model.modelId}
                  versionId={model.modelVersionId}
                  title={model.title}
                  version={`v${model.version}`}
                  dataset={model.dataName}
                  accuracy={model.accuracy}
                  updatedAt={model.updatedAt}
                  createdAt={model.createdAt}
                />
              ) : (
                // model.modelName이 있는 경우
                <WorkspaceCard
                  key={model.modelId}
                  modelId={model.modelId}
                  versionId={model.latestVersionId}
                  title={model.modelName}
                  version={`v${model.latestVersion}`}
                  dataset={model.dataName}
                  accuracy={model.accuracy}
                  updatedAt={model.updatedAt}
                  createdAt={model.createdAt}
                />
              ),
            )
          )}
        </div>

        <Pagination
          totalItems={data!.totalElements}
          currentPage={currentPage}
          pageCount={10}
          itemCountPerPage={12}
        />
      </div>
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
