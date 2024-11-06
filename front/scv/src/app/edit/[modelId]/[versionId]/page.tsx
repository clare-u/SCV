"use client";
import { useState } from "react";
import BlockList from "@/components/block/BlockList";
import CodeViewer from "@/components/code/CodeViewer";
import Button from "@/components/button/Button";
import Chips from "@/components/chips/Chips";
import { ChipsProps } from "@/components/chips/Chips";
import Badge from "@/components/badge/Badge";
import { BadgeProps } from "@/components/badge/Badge";
import { useCallback } from "react";
import { useDropzone } from "react-dropzone";
import { useBlockStore } from "@/store/blockStore";
import { Dataset } from "@/types";

export default function Edit() {
  // 더미 데이터 정의
  const [title, setTitle] = useState("Model Title");
  const [version] = useState("v1");
  const [dataset] = useState<Dataset>("MNIST");

  const [isEditing, setIsEditing] = useState(false);
  const [isHovered, setIsHovered] = useState(false);
  const [editedTitle, setEditedTitle] = useState(title);

  const { blockListValidation } = useBlockStore();

  const datasetColors: Record<string, ChipsProps["color"]> = {
    Editing: "gray",
    MNIST: "indigo",
    Fashion: "amber",
    "CIFAR-10": "green",
    SVHN: "teal",
    EMNIST: "red",
  };

  const badgeColors: Record<string, BadgeProps["color"]> = {
    Editing: "gray",
    MNIST: "indigo",
    Fashion: "amber",
    "CIFAR-10": "green",
    SVHN: "teal",
    EMNIST: "red",
  };

  const handleSaveTitle = () => {
    setTitle(editedTitle);
    setIsEditing(false);
  };

  const handleCancelEdit = () => {
    setEditedTitle(title);
    setIsEditing(false);
  };

  // 파일 드래그앤드롭
  // acceptedFiles를 File[] 타입으로 지정
  const [filePreviews, setFilePreviews] = useState<string[]>([]);
  const onDrop = useCallback((acceptedFiles: File[]) => {
    const previews = acceptedFiles.map((file) => URL.createObjectURL(file));
    setFilePreviews(previews);
  }, []);
  const [testResult, setTestResult] = useState<string | null>(null);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  // 테스트 버튼 클릭 시 결과 생성
  const handleTest = () => {
    setTestResult("출력: 6");
  };

  return (
    <div className="flex h-screen w-full flex-1 flex-col">
      <div className="flex h-[8vh] w-full items-center justify-between border-b border-gray-500 px-10">
        <div className="flex items-center justify-between">
          <div className="flex items-center justify-center gap-10">
            {/* 제목 영역 */}
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
                    onClick={() => setIsEditing(true)}
                  >
                    {title}
                  </h3>
                  {isHovered && (
                    <span
                      className="material-symbols-outlined cursor-pointer"
                      onClick={() => setIsEditing(true)}
                    >
                      edit
                    </span>
                  )}
                </div>
              )}
            </div>
            <Badge color={badgeColors[dataset]}>{version}</Badge>
            <Chips color={datasetColors[dataset]} design="fill">
              {dataset}
            </Chips>
          </div>
        </div>
        <div className="flex items-center gap-10 px-10">
          <Button size="m" design="outline" color="indigo" icon="save_alt">
            새로운 버전으로 저장
          </Button>
          <Button size="m" design="fill" color="indigo" icon="save">
            저장
          </Button>
          <Button
            size="m"
            design="fill"
            color="green"
            icon="play_arrow"
            onClick={() => blockListValidation(dataset)}
          >
            실행
          </Button>
        </div>
      </div>
      <div className="flex flex-1">
        <BlockList dataset={dataset} />
        <div className="flex w-[600px] flex-col border-l border-gray-500">
          <div className="flex max-h-[450px] w-full flex-1 overflow-y-auto overflow-x-hidden border-b border-gray-500">
            <CodeViewer></CodeViewer>
          </div>
          <div className="flex items-center gap-10 border-b border-gray-500 p-10">
            <div className="text-20 font-semibold">실행 결과 : </div>
            <div className="text-20 font-semibold">98.50%</div>
            <div className="text-20">(9583/10000)</div>
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

              {/* 파일 미리보기 */}
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

              {/* 파일이 업로드되면 테스트 버튼 표시 */}
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

              {/* 테스트 결과 표시 */}
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
