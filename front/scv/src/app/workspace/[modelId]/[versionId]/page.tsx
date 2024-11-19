"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import {
  useFetchModelVersions,
  useFetchVersionDetails,
  useExportModel,
} from "@/hooks";
import { Dataset } from "@/types";
import ListboxComponent from "@/components/input/ListBoxComponent";
import Loading from "@/components/loading/Loading";
import Chips from "@/components/chips/Chips";
import type { ChipsProps } from "@/components/chips/Chips";
import Button from "@/components/button/Button";
import Dropdown from "@/components/dropdown/Dropdown";
import CodeViewer from "@/components/code/CodeViewer";
import BlockItem from "@/components/block/BlockItem";
import { convertApiToBlocks, findBlockCategory } from "@/utils/block-converter";
import FeatureActivationViewer from "@/components/canvas/FeatureActivationViewer";
import ActivationMaximizationViewer from "@/components/canvas/ActivationMaximizationViewer";
import ConfusionMatrixTable from "@/components/canvas/ConfusionMatrixTable";

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

  // github export
  const { mutate: exportModelMutation, isPending } = useExportModel();

  // 블록 코드를 내보내는 함수
  const getBlockCode = () => {
    if (!versionData?.resultResponseWithImages.codeView) return "";
    return JSON.parse(versionData.resultResponseWithImages.codeView);
  };

  // 유사모델 찾기
  const handleMatchClick = () => {
    router.push(`/match/${params.modelId}/${params.versionId}`);
  };

  ///////////////////

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
            params: block.params.map((param) => ({
              ...param,
              value: param.value ?? 0,
            })),
            tooltip: block.tooltip,
          }}
          category={findBlockCategory(block.name) || "Basic"} // 기본값 설정
          open={true}
          isEditable={false}
          onBlurParam={(paramIndex, value) => {}}
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

  // layerParams를 파싱하여 number[]로 변환 (resultResponseWithImages가 존재할 경우에만)
  const layerParams: number[] = versionData?.resultResponseWithImages
    ?.layerParams
    ? Array.isArray(versionData.resultResponseWithImages.layerParams)
      ? versionData.resultResponseWithImages.layerParams
      : JSON.parse(versionData.resultResponseWithImages.layerParams)
    : [];

  // Training History 데이터를 조건부로 파싱 (resultResponseWithImages가 존재할 경우에만)
  const trainingHistory = versionData?.resultResponseWithImages?.trainInfos
    ? typeof versionData.resultResponseWithImages.trainInfos === "string"
      ? JSON.parse(versionData.resultResponseWithImages.trainInfos)
          .training_history
      : versionData.resultResponseWithImages.trainInfos.training_history
    : [];

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
          <Button
            size="l"
            design="outline"
            color="indigo"
            icon="upload"
            disabled={isPending}
            onClick={() => {
              exportModelMutation({
                dataName: modelData.DataName as Dataset,
                modelName: modelData.modelName,
                versionNo:
                  modelData.modelVersions.find(
                    (v) => v.versionId === Number(params.versionId),
                  )?.versionNo || 1,
                content: getBlockCode(),
              });
            }}
          >
            깃허브로 내보내기
          </Button>
          <Button
            size="l"
            design="fill"
            color="indigo"
            icon="search"
            onClick={handleMatchClick}
          >
            유사모델 찾기
          </Button>
          <Dropdown modelId={params.modelId} versionId={params.versionId} />
        </div>
      </div>

      {/* Content */}
      <div className="flex w-full flex-col gap-[30px]">
        {/* Model Architecture */}

        <div className="flex w-full justify-center">
          <div className="flex w-[480px] flex-col items-center gap-2 rounded-10 bg-stone-100 p-10">
            {renderModelArchitecture()}
          </div>
          <div className="flex w-1 self-stretch border border-gray-300"></div>

          {/* Code View */}
          {versionData?.resultResponseWithImages?.codeView && (
            <div className="ml-20 w-[480px] items-center justify-center">
              <CodeViewer
                codeString={
                  versionData.resultResponseWithImages.codeView
                    .replace(/^"|"$/g, "")
                    .replace(/\\n/g, "\n") // \n을 줄바꿈으로 변환
                    .replace(/\\t/g, "\t") // \t를 탭으로 변환
                }
              />
            </div>
          )}
        </div>

        {/* Results and Visualizations */}
        {versionData?.resultResponseWithImages && (
          <>
            <div className="border-b border-gray-500 text-24 font-bold text-indigo-900">
              <div className="p-10">결과 열람</div>
            </div>
            {/* Metrics */}
            <div className="flex flex-col gap-10 px-20">
              <div className="text-24 font-bold text-indigo-900">
                Test Accuracy
              </div>
              <div>
                {versionData.resultResponseWithImages.testAccuracy.toFixed(2)}%
              </div>
              <div className="text-24 font-bold text-indigo-900">Test Loss</div>
              <p>{versionData.resultResponseWithImages.testLoss.toFixed(2)}</p>
              <div className="text-24 font-bold text-indigo-900">
                Total Parameters
              </div>
              <p>
                {versionData.resultResponseWithImages.totalParams.toLocaleString()}
              </p>
            </div>

            {/* Feature Activation */}
            {versionData?.resultResponseWithImages?.featureActivation && (
              <div className="flex flex-col px-20">
                <div className="text-24 font-bold text-indigo-900">
                  Feature Activation
                </div>
                <FeatureActivationViewer
                  featureActivation={
                    versionData.resultResponseWithImages.featureActivation
                  }
                />
              </div>
            )}

            {/* Activation Maximization */}
            {versionData.resultResponseWithImages.activationMaximization && (
              <div className="flex flex-col px-20">
                <div className="text-24 font-bold text-indigo-900">
                  Activation Maximization
                </div>
                <ActivationMaximizationViewer
                  activationData={
                    typeof versionData.resultResponseWithImages
                      .activationMaximization === "string"
                      ? versionData.resultResponseWithImages
                          .activationMaximization
                      : JSON.stringify(
                          versionData.resultResponseWithImages
                            .activationMaximization,
                        )
                  }
                />
              </div>
            )}

            {/* Confusion Matrix */}
            {versionData.resultResponseWithImages.confusionMatrix && (
              <div className="flex flex-col px-20">
                <div className="text-24 font-bold text-indigo-900">
                  Confusion Matrix
                </div>
                <ConfusionMatrixTable
                  data={versionData.resultResponseWithImages.confusionMatrix}
                ></ConfusionMatrixTable>
              </div>
            )}

            {/* Example Images */}
            {/* {versionData.resultResponseWithImages.exampleImg && (
              <ExampleImagesComponent
                data={versionData.resultResponseWithImages.exampleImg}
              />
            )} */}

            {/* Training History */}
            {versionData.resultResponseWithImages.trainInfos
              ?.training_history && (
              <div className="mt-6 flex flex-col px-20">
                <div className="text-24 font-bold text-indigo-900">
                  Training History
                </div>
                <div className="space-y-4">
                  {versionData.resultResponseWithImages.trainInfos.training_history.map(
                    (info, index) => (
                      <div key={index} className="rounded-lg border p-4">
                        <p className="font-semibold">Epoch: {info.epoch}</p>
                        <p>Test Loss: {info.test_loss.toFixed(2)}</p>
                        <p>Train Loss: {info.train_loss.toFixed(2)}</p>
                        <p>Test Accuracy: {info.test_accuracy.toFixed(2)}%</p>
                        <p>Train Accuracy: {info.train_accuracy.toFixed(2)}%</p>
                      </div>
                    ),
                  )}
                </div>
              </div>
            )}

            <div className="flex flex-col px-20">
              <div className="text-24 font-bold text-indigo-900">
                Training History
              </div>
              <div className="space-y-4">
                {trainingHistory.map((info: any, index: any) => (
                  <div key={index} className="rounded-lg border p-4">
                    <p className="font-semibold">Epoch: {info.epoch}</p>
                    <p>Test Loss: {info.test_loss.toFixed(2)}</p>
                    <p>Train Loss: {info.train_loss.toFixed(2)}</p>
                    <p>Test Accuracy: {info.test_accuracy.toFixed(2)}%</p>
                    <p>Train Accuracy: {info.train_accuracy.toFixed(2)}%</p>
                  </div>
                ))}
              </div>
            </div>

            <div className="flex flex-col px-20">
              <div className="text-24 font-bold text-indigo-900">
                Layer Parameters
              </div>
              <ul className="mt-4 list-inside list-disc">
                {layerParams.map((param, index) => (
                  <li key={index}>
                    Layer {index + 1}: {param.toLocaleString()} parameters
                  </li>
                ))}
              </ul>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
