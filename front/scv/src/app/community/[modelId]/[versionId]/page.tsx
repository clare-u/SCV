"use client";

import { useState, useEffect } from "react";
import { useFetchModelVersions } from "@/hooks";
import ListboxComponent from "@/components/input/ListBoxComponent";
import Loading from "@/components/loading/Loading";

interface PageProps {
  params: {
    modelId: number;
    versionId: number;
  };
}

export default function CommunityDetail({ params }: PageProps) {
  const modelId = params.modelId;
  const { data: versions, isLoading, error } = useFetchModelVersions(modelId);

  // 버전 데이터를 Listbox 옵션 형태로 변환
  const versionOptions =
    versions?.map((version) => ({
      id: version.versionId,
      name: `v${version.versionNo}`,
    })) || [];

  // 현재 선택된 버전 상태 관리
  const [selectedVersion, setSelectedVersion] = useState(
    versionOptions[0] || { id: 0, name: "버전을 선택하세요" },
  );

  // versions가 로드되면 첫 번째 버전을 선택
  useEffect(() => {
    if (versionOptions.length > 0) {
      setSelectedVersion(versionOptions[0]);
    }
  }, [versions]);

  if (isLoading) return <Loading />;
  if (error) return <div>에러가 발생했습니다: {error.message}</div>;
  if (!versions || versions.length === 0)
    return <div>버전 정보가 없습니다.</div>;

  const handleVersionChange = (version: { id: number; name: string }) => {
    setSelectedVersion(version);
    // 필요한 경우 버전 변경 시 추가 작업 수행
  };

  return (
    <div className="gap-30 flex w-[1100px] flex-col px-10 py-20">
      <div className="w-[100px]">
        <ListboxComponent
          value={selectedVersion}
          onChange={handleVersionChange}
          options={versionOptions}
        />
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
