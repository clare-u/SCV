"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Button from "@/components/button/Button";
import Badge from "@/components/badge/Badge";
import Chips from "@/components/chips/Chips";
import SearchInput from "@/components/input/SearchInput";
import ModalInput from "@/components/input/ModalInput";
import ListboxComponent from "@/components/input/ListBoxComponent";
import { Listbox } from "@headlessui/react";

export default function Home() {
  // 검색 인풋
  const router = useRouter();

  const handleSearchSubmit = (value: string) => {
    router.push(`/?keyword=${value}`);
  };

  // 모달 인풋
  const [inputValue, setInputValue] = useState("");

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const [input2Value, setInput2Value] = useState("");

  const handleInput2Change = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInput2Value(e.target.value);
  };

  // 리스트박스
  const option = [
    { id: 1, name: "새 레포지토리와 연동" },
    { id: 2, name: "기존 레포지토리와 연동" },
  ];

  const version = [
    { id: 1, name: "v1" },
    { id: 2, name: "v2" },
    { id: 3, name: "v3" },
    { id: 4, name: "v4" },
    { id: 5, name: "v5" },
    { id: 6, name: "v6" },
  ];

  const [selectedOption, setSelectedOption] = useState(option[0]);

  const [selectedVersion, setSelectedVersion] = useState(version[0]);

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
      <div className="flex flex-col gap-10">
        <SearchInput placeholder="placeholder" onSubmit={handleSearchSubmit} />
        <ModalInput
          placeholder="placeholder"
          value={inputValue}
          onChange={handleInputChange}
        />
        <ModalInput
          placeholder="placeholder"
          value={input2Value}
          onChange={handleInput2Change}
          color="dark"
        />
        <ListboxComponent
          value={selectedOption}
          onChange={setSelectedOption}
          options={option}
          color="dark"
        />
        <ListboxComponent
          value={selectedVersion}
          onChange={setSelectedVersion}
          options={version}
        />
      </div>
    </div>
  );
}
