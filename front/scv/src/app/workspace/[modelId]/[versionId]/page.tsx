"use client";

import { useState, useEffect } from "react";
import { useFetchModelVersions } from "@/hooks";
import ListboxComponent from "@/components/input/ListBoxComponent";
import Loading from "@/components/loading/Loading";
import Chips from "@/components/chips/Chips";
import { ChipsProps } from "@/components/chips/Chips";
import Button from "@/components/button/Button";
import Dropdown from "@/components/dropdown/Dropdown";

interface PageProps {
  params: {
    modelId: number;
    versionId: number;
  };
}

export default function WorkspaceDetail({ params }: PageProps) {
  const modelId = params.modelId;
  const { data, isLoading, error } = useFetchModelVersions(modelId);

  console.log("버전 데이터: ", data);

  // 버전 데이터를 Listbox 옵션 형태로 변환
  const versionOptions =
    data?.modelVersions?.map((version) => ({
      id: version.versionId,
      name: `v${version.versionNo}`,
    })) || [];

  // 현재 선택된 버전 상태 관리
  const [selectedVersion, setSelectedVersion] = useState(
    versionOptions[0] || { id: 0, name: "버전을 선택하세요" },
  );

  // data 로드되면 첫 번째 버전을 선택
  useEffect(() => {
    if (versionOptions.length > 0) {
      setSelectedVersion(versionOptions[0]);
    }
  }, [data]);

  if (isLoading) return <Loading />;
  if (error) return <div>에러가 발생했습니다: {error.message}</div>;
  if (!data || data.modelVersions.length === 0)
    return <div>모델의 버전 정보가 없습니다.</div>;

  const handleVersionChange = (version: { id: number; name: string }) => {
    setSelectedVersion(version);
    // 필요한 경우 버전 변경 시 추가 작업 수행
  };

  const datasetColors: Record<string, ChipsProps["color"]> = {
    Editing: "gray",
    MNIST: "indigo",
    Fashion: "amber",
    CIFAR10: "green",
    SVHN: "teal",
    EMNIST: "red",
  };

  return (
    <div className="flex w-[1100px] flex-col gap-[30px] px-10 py-20">
      <div className="flex justify-between self-stretch">
        <div className="flex items-center gap-20">
          <div className="text-40 font-bold text-indigo-900">
            {data.modelName}
          </div>
          <div className="w-[100px]">
            <ListboxComponent
              value={selectedVersion}
              onChange={handleVersionChange}
              options={versionOptions}
            />
          </div>
          <Chips color={datasetColors[data.DataName]} design="fill">
            {data.DataName}
          </Chips>
        </div>
        <div className="flex items-center gap-20">
          <Button size="l" design="fill" color="indigo" icon="search">
            유사모델 찾기
          </Button>
          <Dropdown modelId={params.modelId} versionId={params.versionId} />
        </div>
      </div>
      {/* 선택된 버전에 대한 추가 정보를 표시하고 싶다면 */}
      <div className="mt-4">
        <h2 className="text-lg font-medium">선택된 버전 정보</h2>
        <p>버전 ID: {selectedVersion.id}</p>
        {/* 추가 버전 정보 표시 */}
      </div>
    </div>
  );
}
