"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Button from "@/components/button/Button";
import Badge from "@/components/badge/Badge";
import Chips from "@/components/chips/Chips";
import SearchInput from "@/components/input/SearchInput";

export default function Home() {
  // 검색 인풋
  const router = useRouter();
  const [searchValue, setSearchValue] = useState("");

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchValue(e.target.value);
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      router.push(`/?keyword=${searchValue}`);
    }
  };

  return (
    <div className="flex flex-col gap-10 p-20">
      {/* Indigo fill 버튼 */}
      <div className="flex gap-10">
        <Button size="l" design="fill" color="indigo" icon="add_box">
          Indigo Fill Large
        </Button>
        <Button size="m" design="fill" color="indigo" icon="add_box">
          Indigo Fill Medium
        </Button>
        <Button size="s" design="fill" color="indigo" icon="add_box">
          Indigo Fill Small
        </Button>
      </div>

      {/* Green fill and outline 버튼 */}
      <div className="flex gap-10">
        <Button size="m" design="fill" color="green" icon="add_box">
          Green Fill
        </Button>
        <Button size="m" design="outline" color="green" icon="add_box">
          Green Outline
        </Button>
      </div>

      {/* Disabled Button */}
      <div className="flex gap-10">
        <Button size="m" design="fill" color="red" disabled icon="add_box">
          Disabled Button
        </Button>
      </div>

      {/* Badge */}
      <div className="flex gap-10">
        <Badge color="red">Badge</Badge>
        <Badge color="gray">Badge</Badge>
        <Badge color="green">Badge</Badge>
        <Badge color="blue">Badge</Badge>
        <Badge color="amber">Badge</Badge>
        <Badge color="teal">Badge</Badge>
      </div>

      {/* Chips */}
      <div className="flex gap-10">
        <Chips color="indigo" design="fill">
          Chips
        </Chips>
        <Chips color="teal" design="fill">
          Chips
        </Chips>
        <Chips color="gray" design="fill">
          Chips
        </Chips>
        <Chips color="amber" design="fill">
          Chips
        </Chips>
        <Chips color="red" design="fill">
          Chips
        </Chips>
        <Chips color="green" design="fill">
          Chips
        </Chips>
        <Chips color="black" design="fill">
          Chips
        </Chips>
      </div>

      <div className="flex gap-10">
        <Chips color="indigo" design="outline">
          Chips
        </Chips>
        <Chips color="teal" design="outline">
          Chips
        </Chips>
        <Chips color="gray" design="outline">
          Chips
        </Chips>
        <Chips color="amber" design="outline">
          Chips
        </Chips>
        <Chips color="red" design="outline">
          Chips
        </Chips>
        <Chips color="green" design="outline">
          Chips
        </Chips>
        <Chips color="black" design="outline">
          Chips
        </Chips>
      </div>

      {/* Input */}
      <div className="flex gap-10">
        <SearchInput
          placeholder="placeholder"
          value={searchValue}
          onChange={handleSearchChange}
          handleKeyDown={handleKeyDown}
        ></SearchInput>
      </div>
    </div>
  );
}
