"use client";

import { useState, useEffect } from "react";
import { MatchModelData } from "@/types";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { useFetchModelVersions, useFetchVersionDetails } from "@/hooks";
import ListboxComponent from "@/components/input/ListBoxComponent";
import Loading from "@/components/loading/Loading";
import Chips from "@/components/chips/Chips";
import type { ChipsProps } from "@/components/chips/Chips";
import CodeViewer from "@/components/code/CodeViewer";
import BlockItem from "@/components/block/BlockItem";
import { convertApiToBlocks, findBlockCategory } from "@/utils/block-converter";
import { Tab, TabGroup, TabList, TabPanel, TabPanels } from "@headlessui/react";
import Tippy from "@tippyjs/react";
import "tippy.js/dist/tippy.css";
import MarkdownRenderer from "@/components/markdown/MarkdownRenderer";
import Badge from "@/components/badge/Badge";
import { BadgeProps } from "@/components/badge/Badge";
import { toast } from "sonner";

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

const badgeColors: Record<string, BadgeProps["color"]> = {
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
  const [matchModelVersionId, setMatchModelVersionId] = useState<number | null>(
    null,
  );
  const [matchModelModelId, setMatchModelModelId] = useState<number | null>(
    null,
  );

  // 내 모델 정보 불러오기
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
  console.log("versionData:", versionData);

  const [selectedBlockIndex, setSelectedBlockIndex] = useState<number | null>(
    null,
  ); // 클릭된 블록의 인덱스 상태

  // 유사 모델 찾기
  const fetchMatchModelData = async (layerId: number) => {
    const response = await fetch(
      // `http://localhost:8001/${params.modelId}/${params.versionId}/${layerId}/search`,
      `https://k11a107.p.ssafy.io/fast/v1/model/match/${params.modelId}/${params.versionId}/${layerId}/search`,
      {
        method: "GET",
      },
    );

    if (!response.ok) {
      throw new Error("Request failed: " + response.statusText);
    }

    const data: MatchModelData = await response.json();
    return data;
  };

  const {
    data: matchModelData,
    isLoading: matchModelLoading,
    error: matchModelError,
  } = useQuery({
    queryKey: [
      "matchModelData",
      params.modelId,
      params.versionId,
      selectedBlockIndex,
    ],
    queryFn: () => fetchMatchModelData(selectedBlockIndex!),
    enabled: selectedBlockIndex !== null,
    refetchOnWindowFocus: false,
  });

  const handleBlockClick = (blockName: string, index: number) => {
    if (blockName === "nn.Conv2d") {
      setSelectedBlockIndex(index);
    } else {
      toast.error("nn.Conv2d 레이어만 유사 모델 탐색이 가능합니다.");
    }
  };

  // matchModelData가 변경될 때마다 matchModelVersionId 업데이트
  useEffect(() => {
    const modelVersionId = matchModelData?.model_version_id;
    if (modelVersionId) {
      // 모델 ID와 버전 ID 추출
      const versionIdMatch = modelVersionId.match(/(\d+)v(\d+)$/);
      if (versionIdMatch && versionIdMatch.length === 3) {
        const modelId = parseInt(versionIdMatch[1], 10);
        const versionId = parseInt(versionIdMatch[2], 10);
        console.log("Extracted modelId:", modelId, "and versionId:", versionId);
        setMatchModelModelId(modelId);
        setMatchModelVersionId(versionId);
      }
    }
  }, [matchModelData]);

  // 유사모델 정보 불러오기 - matchModelVersionId가 있을 때만 호출
  const fetchVersionDetails = async (versionId: number) => {
    const response = await fetch(
      // `http://localhost:8080/api/v1/models/versions/${versionId}`,
      `https://k11a107.p.ssafy.io/api/v1/models/versions/${versionId}`,
      {
        credentials: "include", // 쿠키 및 인증 정보를 요청과 함께 보내기 위해 추가
      },
    );
    if (!response.ok) {
      console.log("HTTP response not OK, status:", response.status); // 오류 상태 로깅
      throw new Error("Network response was not ok");
    }
    const data = await response.json();
    return data;
  };

  const {
    data: matchModelVersionQuery,
    isLoading: matchModelVersionLoading,
    error: matchModelVersionError,
  } = useQuery({
    queryKey: ["matchModelVersion", matchModelVersionId],
    queryFn: () => fetchVersionDetails(matchModelVersionId!),
    enabled: !!matchModelVersionId,
  });

  // 유사모델의 모델 정보 불러오기 - matchModelVersionId가 있을 때만 호출
  const fetchModelDetails = async (modelId: number) => {
    const response = await fetch(
      // `http://localhost:8080/api/v1/models/${modelId}`,
      `https://k11a107.p.ssafy.io/api/v1/models/${modelId}`,
      {
        credentials: "include", // 쿠키 및 인증 정보를 요청과 함께 보내기 위해 추가
      },
    );
    if (!response.ok) {
      console.log("HTTP response not OK, status:", response.status); // 오류 상태 로깅
      throw new Error("Network response was not ok");
    }
    const data = await response.json();
    return data;
  };

  const {
    data: matchModelModelQuery,
    isLoading: matchModelModelLoading,
    error: matchModelModelError,
  } = useQuery({
    queryKey: ["matchModelVersion", matchModelModelId],
    queryFn: () => fetchModelDetails(matchModelModelId!),
    enabled: !!matchModelModelId,
  });

  // matchModelVersionData를 별도의 변수로 추출
  const matchModelVersionData = matchModelVersionQuery;
  // console.log(matchModelData);
  const handleVersionChange = (version: Version) => {
    router.push(`/match/${params.modelId}/${version.id}`);
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
                  `/match/${params.modelId}/${modelData.modelVersions[0].versionId}`,
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

  // 내 모델 블록
  const renderModelArchitecture = () => {
    try {
      if (!versionData || !versionData?.layers) return null;

      const blocks = convertApiToBlocks({ layers: versionData.layers });

      return blocks.map((block, index) => (
        <div
          onClick={() => handleBlockClick(block.name, index)} // 클릭 시 핸들러 호출
          className={`${
            selectedBlockIndex === index
              ? "rounded-12 border-4 border-blue-900 p-2"
              : ""
          }`} // 조건부 테두리 스타일 적용
        >
          <BlockItem
            key={`${block.name}-${index}`}
            block={{
              name: block.name,
              params: block.params.map((param) => ({
                ...param,
                value: param.value ?? 0,
              })),
            }}
            category={findBlockCategory(block.name) || "Basic"} // 기본값 설정
            open={true}
            isEditable={false}
            onBlurParam={(paramIndex, value) => {
              console.log(`Layer ${index}, Param ${paramIndex}: ${value}`);
            }}
          />
        </div>
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

  // 유사 모델 블록
  const renderMatchModelArchitecture = () => {
    if (matchModelLoading) return <Loading />;
    if (matchModelError)
      return (
        <div>Error loading match model data: {matchModelError.message}</div>
      );
    if (!matchModelData) return null;

    const blocks = convertApiToBlocks({ layers: matchModelData.layers });

    return blocks.map((block, index) => (
      <div
        key={`${block.name}-${index}`}
        className={`${
          matchModelData.layer_id === index
            ? "rounded-12 border-4 border-blue-900 p-2"
            : ""
        }`}
      >
        <BlockItem
          block={{
            name: block.name,
            params: block.params.map((param) => ({
              ...param,
              value: param.value ?? 0,
            })),
          }}
          category={findBlockCategory(block.name) || "Basic"}
          open={true}
          isEditable={false}
          onBlurParam={(paramIndex, value) => {
            console.log(`Layer ${index}, Param ${paramIndex}: ${value}`);
          }}
        />
      </div>
    ));
  };

  return (
    <div className="flex w-[1100px] flex-col gap-[30px] px-10 py-20">
      <header className="w-full py-10 text-[32px] font-bold text-indigo-900">
        유사모델 찾기
      </header>

      <div className="flex w-full flex-col gap-[30px]">
        <div className="flex w-full justify-center">
          {/* 내 모델 */}
          <div className="flex w-[480px] flex-col items-center gap-2 rounded-10 p-10">
            {/* Header */}
            <div className="text-40 font-bold text-indigo-900">내 모델</div>
            <div className="mb-20 flex justify-between self-stretch">
              <div className="flex items-center gap-20">
                <div className="text-[32px] font-bold text-indigo-900">
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
            </div>

            <TabGroup className="flex w-[480px] flex-col">
              <TabList className="flex items-center gap-10 p-10">
                <Tab className="rounded-10 px-20 py-10 data-[selected]:bg-blue-900 data-[selected]:text-white data-[hover]:underline">
                  블록뷰
                </Tab>
                <Tab className="rounded-10 px-20 py-10 data-[selected]:bg-blue-900 data-[selected]:text-white data-[hover]:underline">
                  코드뷰
                </Tab>
                <Tippy content="유사 모델 조회를 원하는 레이어를 블록뷰에서 클릭하세요">
                  <span className="material-symbols-outlined cursor-pointer text-black">
                    help
                  </span>
                </Tippy>
              </TabList>
              <TabPanels>
                <TabPanel>
                  <div className="flex w-full flex-col items-center justify-center gap-2 p-10">
                    {renderModelArchitecture()}
                  </div>
                </TabPanel>
                <TabPanel>
                  {/* Code View */}
                  {versionData?.resultResponseWithImages?.codeView && (
                    <div className="h-[600px] items-center justify-center py-10">
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
                </TabPanel>
              </TabPanels>
            </TabGroup>
          </div>

          <div className="flex w-1 self-stretch border border-gray-300"></div>

          <div className="flex w-[480px] flex-col items-center gap-2 rounded-10 p-10">
            <div className="text-40 font-bold text-indigo-900">유사 모델</div>

            {matchModelLoading || matchModelVersionLoading ? (
              <div className="flex h-[600px] items-center justify-center">
                <Loading />
              </div>
            ) : matchModelError || matchModelVersionError ? (
              <div className="flex h-[600px] items-center justify-center text-center text-red-500">
                <div>
                  <p className="mb-2 font-bold">오류가 발생했습니다</p>
                  <p>
                    {matchModelError?.message ||
                      matchModelVersionError?.message}
                  </p>
                </div>
              </div>
            ) : selectedBlockIndex === null ? (
              <div className="flex h-[600px] items-center justify-center text-center text-gray-500">
                <p>
                  왼쪽의 레이어를 선택하면
                  <br />
                  유사한 모델이 표시됩니다.
                </p>
              </div>
            ) : matchModelVersionData ? (
              <>
                <div className="mb-20 flex justify-between self-stretch">
                  <div className="flex items-center gap-20">
                    <div className="text-[32px] font-bold text-indigo-900">
                      {matchModelModelQuery.modelName}
                    </div>
                    <Badge color={badgeColors[matchModelModelQuery.DataName]}>
                      v{matchModelModelQuery.latestVersion}
                    </Badge>
                    <Chips
                      color={datasetColors[matchModelModelQuery.DataName]}
                      design="fill"
                    >
                      {matchModelModelQuery.DataName}
                    </Chips>
                  </div>
                </div>
                <TabGroup className="flex w-[480px] flex-col">
                  <TabList className="flex gap-10 p-10">
                    <Tab className="rounded-10 px-20 py-10 data-[selected]:bg-blue-900 data-[selected]:text-white data-[hover]:underline">
                      블록뷰
                    </Tab>
                    <Tab className="rounded-10 px-20 py-10 data-[selected]:bg-blue-900 data-[selected]:text-white data-[hover]:underline">
                      코드뷰
                    </Tab>
                  </TabList>
                  <TabPanels>
                    <TabPanel>
                      <div className="flex w-full flex-col items-center justify-center gap-2 p-10">
                        {renderMatchModelArchitecture()}
                      </div>
                    </TabPanel>
                    <TabPanel>
                      {matchModelVersionData?.resultResponseWithImages
                        ?.codeView ? (
                        <div className="h-[600px] items-center justify-center py-10">
                          <CodeViewer
                            codeString={matchModelVersionData.resultResponseWithImages.codeView
                              .replace(/^"|"$/g, "")
                              .replace(/\\n/g, "\n")
                              .replace(/\\t/g, "\t")}
                          />
                        </div>
                      ) : (
                        <div className="flex h-[600px] items-center justify-center text-gray-500">
                          코드 정보를 찾을 수 없습니다.
                        </div>
                      )}
                    </TabPanel>
                  </TabPanels>
                </TabGroup>
              </>
            ) : (
              <div className="flex h-[600px] items-center justify-center text-gray-500">
                유사한 모델을 찾을 수 없습니다.
              </div>
            )}
          </div>
        </div>

        {/* Results and Visualizations */}
        <div className="flex flex-col gap-4 rounded-10 p-6">
          <h2 className="text-2xl font-bold text-indigo-900">GPT 설명</h2>
          <div className="min-h-[100px] rounded-md bg-white p-4">
            {matchModelLoading ? (
              <div className="flex h-full items-center justify-center">
                <Loading />
              </div>
            ) : matchModelError ? (
              <div className="text-red-500">
                설명을 불러오는 중 오류가 발생했습니다:{" "}
                {matchModelError.message}
              </div>
            ) : matchModelData?.gpt_description ? (
              <MarkdownRenderer
                markdownText={matchModelData.gpt_description}
              ></MarkdownRenderer>
            ) : selectedBlockIndex === null ? (
              <div className="text-gray-500">
                레이어를 선택하면 유사 모델에 대한 설명이 표시됩니다.
              </div>
            ) : (
              <div className="text-gray-500">설명이 없습니다.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
