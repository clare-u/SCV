"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useFetchModelVersions, useFetchVersionDetails } from "@/hooks";
import ListboxComponent from "@/components/input/ListBoxComponent";
import Loading from "@/components/loading/Loading";
import Chips from "@/components/chips/Chips";
import type { ChipsProps } from "@/components/chips/Chips";
import Image from "next/image";

interface Version {
  id: number;
  name: string;
}

interface PageProps {
  params: {
    modelId: number;
    versionId: number;
  };
}

export default function CommunityDetail({ params }: PageProps) {
  const router = useRouter();
  const [isVersionValid, setIsVersionValid] = useState<boolean | null>(null);

  const {
    data: modelData,
    isLoading: modelLoading,
    error: modelError,
  } = useFetchModelVersions(params.modelId);

  const {
    data: versionData,
    isLoading: versionLoading,
    error: versionError,
  } = useFetchVersionDetails(params.versionId);

  // 버전 데이터를 Listbox 옵션 형태로 변환
  const versionOptions =
    modelData?.modelVersions?.map((version) => ({
      id: version.versionId,
      name: `v${version.versionNo}`,
    })) || [];

  useEffect(() => {
    if (modelData?.modelVersions) {
      const currentVersionId = Number(params.versionId);
      const isValid = modelData.modelVersions.some(
        (version) => Number(version.versionId) === currentVersionId,
      );
      console.log("Version validation:", {
        currentVersionId,
        availableVersions: modelData.modelVersions.map((v) => v.versionId),
        isValid,
      });
      setIsVersionValid(isValid);
    }
  }, [modelData, params.versionId]);

  const isLoading = modelLoading || versionLoading || isVersionValid === null;
  const error = modelError || versionError;

  // versionId가 null인지 확인하는 함수
  const isNullVersion = () => {
    return (
      params.versionId === null ||
      params.versionId === undefined ||
      Number.isNaN(Number(params.versionId))
    );
  };

  // null 체크를 먼저 수행
  if (isNullVersion()) {
    return (
      <div className="flex flex-col items-center justify-center">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-gray-900">
            유효하지 않은 접근입니다
          </h1>
          <p className="mb-8 text-gray-600">
            버전 정보가 필요합니다. 커뮤니티 목록으로 돌아가주세요.
          </p>
          <button
            onClick={() => router.push("/community")}
            className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700"
          >
            커뮤니티로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  if (isLoading) return <Loading />;

  if (!modelData) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-gray-900">
            모델 정보를 찾을 수 없습니다
          </h1>
          <p className="mb-8 text-gray-600">
            요청하신 모델에 대한 정보를 불러올 수 없습니다.
          </p>
          <button
            onClick={() => router.push("/community")}
            className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700"
          >
            커뮤니티로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  if (!isVersionValid) {
    return (
      <div className="flex flex-col items-center justify-center">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-gray-900">
            잘못된 버전 정보입니다
          </h1>
          <p className="mb-8 text-gray-600">
            해당 모델에 존재하지 않는 버전입니다.
          </p>
          <div className="space-x-4">
            <button
              onClick={() => router.push("/community")}
              className="rounded-md bg-gray-600 px-4 py-2 text-white hover:bg-gray-700"
            >
              커뮤니티로 돌아가기
            </button>
            <button
              onClick={() =>
                router.push(
                  `/community/${params.modelId}/${modelData.modelVersions[0].versionId}`,
                )
              }
              className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700"
            >
              첫 번째 버전으로 이동
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (error) return <div>에러가 발생했습니다: {error.message}</div>;

  // findVersionNo 함수
  const findVersionNo = () => {
    const version = modelData?.modelVersions?.find(
      (version) => version.versionId === Number(params.versionId),
    );

    const versionNo = version?.versionNo ?? 1; // 기본값을 1로 변경
    return `v${versionNo}`;
  };

  // currentVersion 초기 값 설정
  const currentVersion = {
    id: Number(params.versionId), // 숫자로 변환하여 설정
    name: findVersionNo(),
  };

  const handleVersionChange = (version: Version) => {
    router.push(`/workspace/${params.modelId}/${version.id}`);
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
            {modelData.modelName}
          </div>
          <div className="w-[100px]">
            {modelData ? (
              <ListboxComponent
                value={currentVersion}
                onChange={handleVersionChange}
                options={versionOptions}
              />
            ) : (
              <Loading /> // 또는 스켈레톤 UI
            )}
          </div>
          <Chips color={datasetColors[modelData.DataName]} design="fill">
            {modelData.DataName}
          </Chips>
        </div>
        <div className="flex items-center gap-20">
          <div className="flex items-center justify-center gap-10 whitespace-nowrap rounded-10 bg-gray-200 p-10 text-20 font-semibold">
            <Image
              src={modelData.userInfo.userImageUrl}
              alt={modelData.userInfo.userNickname}
              width={50}
              height={50}
              className="rounded-full"
            />
            {modelData.userInfo.userNickname}
          </div>
        </div>
      </div>

      <div className="mt-4">
        <h2 className="text-lg font-medium">Version Details</h2>
        <p>Version ID: {versionData?.modelVersionId}</p>
        <div className="mt-2">
          {versionData?.resultAnalysisResponse && (
            <>
              <p>
                Test Accuracy: {versionData.resultAnalysisResponse.testAccuracy}
              </p>
              <p>Test Loss: {versionData.resultAnalysisResponse.testLoss}</p>
              <p>
                Total Parameters:{" "}
                {versionData.resultAnalysisResponse.totalParams}
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
