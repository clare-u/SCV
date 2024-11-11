"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useFetchModelVersions, useFetchVersionDetails } from "@/hooks";
import ListboxComponent from "@/components/input/ListBoxComponent";
import Loading from "@/components/loading/Loading";
import Chips from "@/components/chips/Chips";
import type { ChipsProps } from "@/components/chips/Chips";
import Button from "@/components/button/Button";
import Dropdown from "@/components/dropdown/Dropdown";
import CanvasComponent from "@/components/canvas/CanvasComponent";
import CodeViewer from "@/components/code/CodeViewer";
import BlockItem from "@/components/block/BlockItem";
import { convertApiToBlocks } from "@/utils/block-converter";

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

// 데이터셋 색상 매핑
const datasetColors: Record<string, ChipsProps["color"]> = {
  Editing: "gray",
  MNIST: "indigo",
  Fashion: "amber",
  CIFAR10: "green",
  SVHN: "teal",
  EMNIST: "red",
};

const ErrorPage = ({
  message,
  buttonText,
  onButtonClick,
}: {
  message: string;
  buttonText: string;
  onButtonClick: () => void;
}) => (
  <div className="flex min-h-screen flex-col items-center justify-center">
    <div className="text-center">
      <h1 className="mb-4 text-2xl font-bold text-gray-900">{message}</h1>
      <button
        onClick={onButtonClick}
        className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700"
      >
        {buttonText}
      </button>
    </div>
  </div>
);

export default function WorkspaceDetail({ params }: PageProps) {
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

  useEffect(() => {
    if (modelData?.modelVersions) {
      const currentVersionId = Number(params.versionId);
      const isValid = modelData.modelVersions.some(
        (version) => Number(version.versionId) === currentVersionId,
      );
      setIsVersionValid(isValid);
    }
  }, [modelData, params.versionId]);

  // 버전 데이터를 Listbox 옵션 형태로 변환
  const versionOptions =
    modelData?.modelVersions?.map((version) => ({
      id: version.versionId,
      name: `v${version.versionNo}`,
    })) || [];

  // 현재 선택된 버전 정보
  const currentVersion = {
    id: Number(params.versionId),
    name: `v${
      modelData?.modelVersions?.find(
        (version) => version.versionId === Number(params.versionId),
      )?.versionNo ?? 1
    }`,
  };

  // 에러 및 로딩 상태 처리
  if (!params.versionId || params.versionId === null) {
    return (
      <ErrorPage
        message="유효하지 않은 접근입니다"
        buttonText="워크스페이스로 돌아가기"
        onButtonClick={() => router.push("/workspace")}
      />
    );
  }

  if (modelLoading || versionLoading) return <Loading />;
  if (modelError || versionError)
    return (
      <div>
        에러가 발생했습니다: {modelError?.message || versionError?.message}
      </div>
    );

  if (!modelData) {
    return (
      <ErrorPage
        message="모델 정보를 찾을 수 없습니다"
        buttonText="워크스페이스로 돌아가기"
        onButtonClick={() => router.push("/workspace")}
      />
    );
  }

  if (modelData.modelVersions.length === 0) {
    return (
      <ErrorPage
        message="모델의 버전 정보가 없습니다"
        buttonText="워크스페이스로 돌아가기"
        onButtonClick={() => router.push("/workspace")}
      />
    );
  }

  if (!isVersionValid) {
    return (
      <div className="flex flex-col items-center justify-center">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-gray-900">
            잘못된 버전 정보입니다
          </h1>
          <div className="space-x-4">
            <button
              onClick={() => router.push("/workspace")}
              className="rounded-md bg-gray-600 px-4 py-2 text-white hover:bg-gray-700"
            >
              워크스페이스로 돌아가기
            </button>
            <button
              onClick={() =>
                router.push(
                  `/workspace/${params.modelId}/${modelData.modelVersions[0].versionId}`,
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

  const handleVersionChange = (version: Version) => {
    router.push(`/workspace/${params.modelId}/${version.id}`);
  };

  const renderModelArchitecture = () => {
    try {
      if (!versionData?.layers) return null;
      const blocks = convertApiToBlocks({ layers: versionData.layers });

      return blocks.map((block, index) => (
        <BlockItem
          key={`${block.name}-${index}`}
          block={{
            name: block.name,
            params: block.params,
          }}
          category={block.category}
          open={true}
          isEditable={false}
          onBlurParam={(paramIndex, value) => {
            console.log(`Layer ${index}, Param ${paramIndex}: ${value}`);
          }}
        />
      ));
    } catch (error) {
      console.error("Error converting blocks:", error);
      return (
        <div className="rounded bg-red-100 p-4 text-red-600">
          <p className="font-bold">Error loading model architecture</p>
          <p>{(error as Error).message}</p>
        </div>
      );
    }
  };

  return (
    <div className="flex w-[1100px] flex-col gap-[30px] px-10 py-20">
      {/* Header */}
      <div className="flex justify-between self-stretch">
        <div className="flex items-center gap-20">
          <div className="text-40 font-bold text-indigo-900">
            {modelData.modelName}
          </div>
          <div className="w-[100px]">
            <ListboxComponent
              value={currentVersion}
              onChange={handleVersionChange}
              options={versionOptions}
            />
          </div>
          <Chips color={datasetColors[modelData.DataName]} design="fill">
            {modelData.DataName}
          </Chips>
        </div>
        <div className="flex items-center gap-20">
          <Button size="l" design="fill" color="indigo" icon="search">
            유사모델 찾기
          </Button>
          <Dropdown modelId={params.modelId} versionId={params.versionId} />
        </div>
      </div>

      {/* Content */}
      <div className="flex w-full flex-col gap-[30px]">
        {/* Model Architecture */}

        <div className="flex w-full justify-center">
          <div className="flex w-[480px] flex-col items-center gap-2 rounded-10 bg-stone-100 px-10">
            {renderModelArchitecture()}
          </div>
          <div className="flex w-1 self-stretch border border-gray-300"></div>

          {/* Code View */}
          {versionData?.resultResponseWithImages?.codeView && (
            <div className="ml-20 w-[480px] items-center justify-center">
              <CodeViewer
                codeString={
                  JSON.parse(versionData.resultResponseWithImages.codeView).code
                }
              />
            </div>
          )}
        </div>

        {/* Results and Visualizations */}
        {versionData?.resultResponseWithImages && (
          <div className="mt-6">
            ``
            {/* Metrics */}
            <div className="flex gap-10">
              <p>
                Test Accuracy:{" "}
                {versionData.resultResponseWithImages.testAccuracy.toFixed(2)}%
              </p>
              <p>
                Test Loss:{" "}
                {versionData.resultResponseWithImages.testLoss.toFixed(2)}
              </p>
              <p>
                Total Parameters:{" "}
                {versionData.resultResponseWithImages.totalParams.toLocaleString()}
              </p>
            </div>
            {/* Feature Activation */}
            {versionData.resultResponseWithImages.featureActivation?.[0]
              ?.origin && (
              <div className="mt-6">
                <h3 className="mb-2 text-lg font-medium">Feature Activation</h3>
                <div className="inline-block border-2 border-gray-500 p-4">
                  <CanvasComponent
                    data={
                      versionData.resultResponseWithImages.featureActivation[0]
                        .origin
                    }
                  />
                </div>
              </div>
            )}
            {/* Activation Maximization */}
            {versionData.resultResponseWithImages.activationMaximization?.[0]
              ?.image && (
              <div className="mt-6">
                <h3 className="mb-2 text-lg font-medium">
                  Activation Maximization
                </h3>
                <div className="inline-block border-2 border-gray-500 p-4">
                  <CanvasComponent
                    data={
                      versionData.resultResponseWithImages
                        .activationMaximization[0].image
                    }
                  />
                </div>
              </div>
            )}
            {/* Confusion Matrix */}
            {versionData.resultResponseWithImages.confusionMatrix && (
              <div className="mt-6">
                <h3 className="mb-2 text-lg font-medium">Confusion Matrix</h3>
                <div className="max-w-1100">
                  <pre>
                    {versionData.resultResponseWithImages.confusionMatrix}
                  </pre>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
