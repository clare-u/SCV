"use client";
import { useState, useEffect, useCallback } from "react";
import BlockList from "@/components/block/BlockList";
import CodeViewer from "@/components/code/CodeViewer";
import Button from "@/components/button/Button";
import Chips from "@/components/chips/Chips";
import { ChipsProps } from "@/components/chips/Chips";
import Badge from "@/components/badge/Badge";
import { BadgeProps } from "@/components/badge/Badge";
import { useDropzone } from "react-dropzone";
import { useBlockStore } from "@/store/blockStore";
import { Dataset } from "@/types";
import { useFetchModelVersions, useFetchVersionDetails } from "@/hooks";
import Loading from "@/components/loading/Loading";
import { useRouter } from "next/navigation";

interface EditProps {
  params: {
    modelId: number;
    versionId: number;
  };
}

export default function Edit({ params }: EditProps) {
  const router = useRouter();
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

  // 실제 제목과 수정용 제목을 분리
  const [title, setTitle] = useState("");
  const [editedTitle, setEditedTitle] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [isHovered, setIsHovered] = useState(false);
  const [filePreviews, setFilePreviews] = useState<string[]>([]);
  const [testResult, setTestResult] = useState<string | null>(null);

  const { blockListValidation } = useBlockStore();

  // modelData가 로드되면 title 초기화
  useEffect(() => {
    if (modelData?.modelName) {
      setTitle(modelData.modelName);
    }
  }, [modelData?.modelName]);

  const datasetColors: Record<string, ChipsProps["color"]> = {
    Editing: "gray",
    MNIST: "indigo",
    Fashion: "amber",
    CIFAR10: "green",
    SVHN: "teal",
    EMNIST: "red",
  };

  const badgeColors: Record<string, BadgeProps["color"]> = {
    Editing: "gray",
    MNIST: "indigo",
    Fashion: "amber",
    CIFAR10: "green",
    SVHN: "teal",
    EMNIST: "red",
  };

  // 편집 모드 시작 시 editedTitle을 현재 title로 설정
  const handleStartEditing = () => {
    setEditedTitle(title);
    setIsEditing(true);
  };

  const handleSaveTitle = () => {
    // TODO: API 연동 시 제목 업데이트 로직 추가
    setTitle(editedTitle);
    setIsEditing(false);
  };

  const handleCancelEdit = () => {
    setEditedTitle(title); // 현재 title로 복원
    setIsEditing(false);
  };

  const onDrop = useCallback((acceptedFiles: File[]) => {
    const previews = acceptedFiles.map((file) => URL.createObjectURL(file));
    setFilePreviews(previews);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  const handleTest = () => {
    setTestResult("출력: 6");
  };

  const [isVersionValid, setIsVersionValid] = useState<boolean | null>(null);

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

  if (modelLoading || versionLoading) return <Loading />;
  if (!modelData || !versionData) return <div>데이터를 찾을 수 없습니다.</div>;

  const currentVersionNo = modelData.modelVersions.find(
    (version) => Number(version.versionId) === Number(params.versionId),
  )?.versionNo;

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
              onClick={() => router.push("/workspace")}
              className="rounded-md bg-gray-600 px-4 py-2 text-white hover:bg-gray-700"
            >
              워크스페이스로 돌아가기
            </button>
            <button
              onClick={() =>
                router.push(
                  `/edit/${params.modelId}/${modelData.modelVersions[0].versionId}`,
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
  if (modelError || versionError) return <div>에러가 발생했습니다.</div>;
  return (
    <div className="flex h-screen w-full flex-1 flex-col">
      <div className="flex h-[8vh] w-full items-center justify-between border-b border-gray-500 px-10">
        <div className="flex items-center justify-between">
          <div className="flex items-center justify-center gap-10">
            <div
              className="relative flex items-center gap-2"
              onMouseEnter={() => setIsHovered(true)}
              onMouseLeave={() => setIsHovered(false)}
            >
              {isEditing ? (
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={editedTitle}
                    onChange={(e) => setEditedTitle(e.target.value)}
                    className="rounded border px-2 py-1 text-20 font-semibold"
                  />
                  <Button
                    size="s"
                    design="fill"
                    color="indigo"
                    onClick={handleSaveTitle}
                    icon="edit"
                  >
                    수정
                  </Button>
                  <Button
                    size="s"
                    design="fill"
                    color="red"
                    onClick={handleCancelEdit}
                    icon="close"
                  >
                    수정 취소
                  </Button>
                </div>
              ) : (
                <div className="flex items-center gap-1">
                  <h3
                    className="cursor-pointer text-20 font-semibold hover:underline"
                    onClick={handleStartEditing}
                  >
                    {title}
                  </h3>
                  {isHovered && (
                    <span
                      className="material-symbols-outlined cursor-pointer"
                      onClick={handleStartEditing}
                    >
                      edit
                    </span>
                  )}
                </div>
              )}
            </div>
            <Badge
              color={badgeColors[modelData.DataName]}
            >{`v${currentVersionNo}`}</Badge>
            <Chips color={datasetColors[modelData.DataName]} design="fill">
              {modelData.DataName}
            </Chips>
          </div>
        </div>
        <div className="flex items-center gap-10 px-10">
          <Button size="m" design="fill" color="indigo" icon="save">
            저장
          </Button>
          <Button
            size="m"
            design="fill"
            color="green"
            icon="play_arrow"
            onClick={() => blockListValidation(modelData.DataName as Dataset)}
          >
            실행
          </Button>
        </div>
      </div>
      <div className="flex h-[92vh]">
        <BlockList dataset={modelData.DataName as Dataset} />
        <div className="flex w-[600px] flex-col border-l border-gray-500">
          <div className="flex max-h-[450px] w-full flex-1 overflow-y-auto overflow-x-hidden border-b border-gray-500">
            <CodeViewer codeString="# 실행 후 이곳에 코드가 나타납니다."></CodeViewer>
          </div>
          <div className="flex items-center gap-10 border-b border-gray-500 p-10">
            <div className="text-20 font-semibold">실행 결과 : </div>
            <div className="text-20 font-semibold">
              {versionData.resultResponseWithImages?.testAccuracy ?? "N/A"}%
            </div>
            <div className="text-20">
              ({versionData.resultResponseWithImages?.totalParams ?? "N/A"}
              /1000000)
            </div>
          </div>
          <div className="flex flex-col justify-center p-10">
            <div className="flex flex-1 border-b border-gray-300 text-20 font-semibold">
              테스트
            </div>
            <div className="flex items-center gap-10 p-10">
              <div
                {...getRootProps()}
                className={`cursor-pointer rounded border-2 border-dashed p-5 ${
                  isDragActive ? "border-green-500" : "border-gray-500"
                }`}
              >
                <input {...getInputProps()} />
                {isDragActive ? (
                  <p>여기에 파일을 드롭하세요...</p>
                ) : (
                  <p>파일을 드래그하거나 클릭하여 업로드하세요</p>
                )}
              </div>

              <div className="flex items-center gap-4">
                {filePreviews.map((preview, index) => (
                  <div
                    key={index}
                    className="h-[50px] w-[50px] overflow-hidden rounded border"
                  >
                    <img
                      src={preview}
                      alt={`Preview ${index}`}
                      className="h-full w-full object-cover"
                    />
                  </div>
                ))}
              </div>

              {filePreviews.length > 0 && (
                <Button
                  size="m"
                  design="fill"
                  color="blue"
                  onClick={handleTest}
                >
                  테스트
                </Button>
              )}

              {testResult && (
                <div className="rounded border border-green-300 p-3 text-18 font-semibold text-green-600">
                  {testResult}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
