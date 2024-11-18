"use client";

import Image from "next/image";
import Link from "next/link";
import Chips from "../chips/Chips";
import { ChipsProps } from "../chips/Chips";
import Badge from "../badge/Badge";
import { BadgeProps } from "../badge/Badge";

interface BoardCardProps {
  modelId: number;
  versionId: number;
  title: string;
  version: string;
  dataset: string;
  profileImg?: string;
  nickname: string;
  accuracy?: number;
  updatedAt: string;
}

const datasetColors: Record<string, ChipsProps["color"]> = {
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

export default function BoardCard({
  modelId,
  versionId,
  title,
  version,
  dataset,
  profileImg,
  nickname,
  accuracy,
  updatedAt,
}: BoardCardProps) {
  return (
    <Link
      href={`/community/${modelId}/${versionId}`}
      passHref
      className="flex w-[325px]"
    >
      <div className="flex w-full flex-col gap-20 rounded-12 bg-gray-100 p-[30px] shadow-md transition-shadow duration-200 hover:shadow-lg">
        <div className="flex items-center justify-between">
          <div className="flex items-center justify-center gap-10">
            <h3 className="text-20 font-semibold">{title}</h3>
            <Badge color={badgeColors[dataset]}>{version}</Badge>
          </div>
          <Chips color={datasetColors[dataset]} design="fill">
            {dataset}
          </Chips>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center gap-10">
            {profileImg ? (
              <Image
                src={profileImg}
                alt={`${nickname}'s profile`}
                width={40}
                height={40}
                className="rounded-full"
              />
            ) : (
              ""
            )}
            <p className="text-14 font-semibold">{nickname}</p>
          </div>
          <div className="flex flex-col items-end justify-center">
            {accuracy && accuracy !== -1 ? (
              <p className="text-12 font-semibold">{accuracy.toFixed(2)}%</p>
            ) : (
              <p></p>
            )}
            <p className="text-12 text-gray-400">
              {new Intl.DateTimeFormat("ko-KR", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
                timeZone: "Asia/Seoul",
              }).format(new Date(updatedAt))}
            </p>
          </div>
        </div>
      </div>
    </Link>
  );
}
