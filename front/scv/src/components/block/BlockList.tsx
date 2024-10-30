"use client";

// components/BlockList.tsx
import { Tab, TabList, TabPanel, TabGroup, TabPanels } from "@headlessui/react";
import { CustomBlockList } from "./CustomBlockList";
import { BlockDefinition, BlockCategory } from "@/types";
import BlockItem from "./BlockItem";

// 카테고리 키에 대해 표시할 이름을 매핑
const categoryDisplayNames: Record<BlockCategory, string> = {
  Convolution: "Convolution Layers",
  Pooling: "Pooling Layers",
  Padding: "Padding Layers",
  Activation: "Non-Linear Activations",
  Linear: "Linear Layers",
};

// 카테고리별 색상 스타일을 매핑
const categoryColors: Record<BlockCategory, string> = {
  Convolution: "border-blue-700 bg-blue-500",
  Pooling: "border-violet-700 bg-violet-500",
  Padding: "border-yellow-700 bg-yellow-500",
  Activation: "border-green-700 bg-green-500",
  Linear: "border-red-700 bg-red-500",
};

const BlockList: React.FC = () => {
  const categories = Object.entries(CustomBlockList) as [
    BlockCategory,
    BlockDefinition[],
  ][];

  return (
    <TabGroup as="div" className="flex">
      {/* 왼쪽에 탭 버튼을 세로로 배치 */}
      <TabList className="flex h-screen flex-col gap-4 border-r border-gray-500 bg-stone-200 p-4">
        {categories.map(([category]) => (
          <Tab
            key={category}
            className={({ selected }) =>
              selected
                ? "flex w-[120px] flex-col items-center gap-[5px] rounded-[20px] border-none bg-stone-400 px-3 py-10 text-16 focus:border-none focus:outline-none"
                : "flex w-[120px] flex-col items-center gap-[5px] rounded-[20px] px-3 py-10 text-16 hover:bg-stone-300"
            }
          >
            <div
              className={`h-[50px] w-[50px] rounded-full border-2 ${categoryColors[category]}`}
            ></div>
            {categoryDisplayNames[category]}
          </Tab>
        ))}
      </TabList>

      {/* 오른쪽에 각 카테고리별 블록 렌더링 */}
      <TabPanels className="flex border-r border-gray-500 bg-stone-200 p-20">
        {categories.map(([category, blocks]) => (
          <TabPanel key={category}>
            <div className="flex flex-col gap-20">
              {blocks.map((block) => (
                <BlockItem
                  key={block.name}
                  block={block}
                  category={category}
                  small={true}
                />
              ))}
            </div>
          </TabPanel>
        ))}
      </TabPanels>
    </TabGroup>
  );
};

export default BlockList;
