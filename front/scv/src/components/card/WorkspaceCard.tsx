"use client";

import { useRouter } from "next/navigation";
import Chips from "../chips/Chips";
import { ChipsProps } from "../chips/Chips";
import Badge from "../badge/Badge";
import { BadgeProps } from "../badge/Badge";
import DeleteDropdown from "../dropdown/DeleteDropdown";

interface WorkspaceCardProps {
  modelId: number;
  versionId: string;
  title: string;
  version: string;
  dataset: string;
  accuracy?: number;
  createdAt: string;
  updatedAt: string;
}

const datasetColors: Record<string, ChipsProps["color"]> = {
  MNIST: "indigo",
  Fashion: "amber",
  CIFAR10: "green",
  SVHN: "teal",
  EMNIST: "red",
  Editing: "gray",
};

const badgeColors: Record<string, BadgeProps["color"]> = {
  MNIST: "indigo",
  Fashion: "amber",
  CIFAR10: "green",
  SVHN: "teal",
  EMNIST: "red",
  Editing: "gray",
};

export default function WorkspaceCard({
  modelId,
  versionId,
  title,
  version,
  dataset,
  accuracy,
  createdAt,
  updatedAt,
}: WorkspaceCardProps) {
  const router = useRouter();
  // Link URL을 조건에 따라 설정
  const href =
    dataset === "Editing"
      ? `/edit/${modelId}/${versionId}`
      : `/workspace/${modelId}/${versionId}`;

  const handleCardClick = (e: React.MouseEvent) => {
    // DeleteDropdown이 클릭된 경우 네비게이션을 막음
    if (!(e.target as HTMLElement).closest(".delete-dropdown")) {
      router.push(href);
    }
  };

  return (
    <div onClick={handleCardClick} className="flex w-[325px] cursor-pointer">
      <div className="flex w-full flex-col gap-20 rounded-12 bg-gray-100 p-[30px] shadow-md transition-shadow duration-200 hover:shadow-lg">
        <div className="flex items-center justify-between">
          <div className="flex items-center justify-center gap-10">
            <h3 className="text-20 font-semibold">{title}</h3>
            <Badge color={badgeColors[dataset]}>{version}</Badge>
            <Chips color={datasetColors[dataset]} design="fill">
              {dataset}
            </Chips>
          </div>
          <DeleteDropdown
            onClick={(e) => e.stopPropagation()}
            modelId={modelId}
          />
        </div>

        <div className="flex items-center justify-between">
          <div className="flex flex-col items-end justify-center">
            <p className="text-12 text-gray-400">
              수정일:{" "}
              {new Date(updatedAt).toISOString().replace("T", " ").slice(0, 19)}
            </p>
            <p className="text-12 text-gray-400">
              생성일:{" "}
              {new Date(createdAt).toISOString().replace("T", " ").slice(0, 19)}
            </p>
          </div>
          {/* dataset이 Editing이 아닐 때만 accuracy를 렌더링 */}
          {dataset !== "Editing" && accuracy && accuracy !== -1 && (
            <div className="flex flex-col items-end justify-center">
              <p className="text-12 font-semibold">{accuracy.toFixed(2)}%</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
