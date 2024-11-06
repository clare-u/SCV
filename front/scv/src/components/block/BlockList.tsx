"use client";
import { useState, useEffect } from "react";
import { Tab, TabPanel, TabGroup, TabList, TabPanels } from "@headlessui/react";
import { DragDropContext, Draggable, Droppable } from "@hello-pangea/dnd";
import { CustomBlockList } from "./CustomBlockList";
import { BlockDefinition, BlockCategory } from "@/types";
import BlockItem from "./BlockItem";
import { useBlockStore } from "@/store/blockStore";

// 카테고리 표시 이름 매핑
const categoryDisplayNames: Record<BlockCategory, string> = {
  Basic: "Basic",
  Convolution: "Convolution Layers",
  Pooling: "Pooling Layers",
  Padding: "Padding Layers",
  Activation: "Non-Linear Activations",
  Linear: "Linear Layers",
};

// 카테고리별 색상 스타일 매핑
const categoryColors: Record<BlockCategory, string> = {
  Basic: "border-gray-700, bg-gray-500",
  Convolution: "border-blue-700 bg-blue-500",
  Pooling: "border-violet-700 bg-violet-500",
  Padding: "border-yellow-700 bg-yellow-500",
  Activation: "border-green-700 bg-green-500",
  Linear: "border-red-700 bg-red-500",
};

interface DroppedBlock extends BlockDefinition {
  id: string;
  category: BlockCategory;
}

const BlockList: React.FC = () => {
  const categories = Object.entries(CustomBlockList).filter(
    ([category]) => category !== "Basic",
  ) as [BlockCategory, BlockDefinition[]][];

  // 기본 블록 정의: start와 end를 고정된 위치로 추가
  const initialBlocks: DroppedBlock[] = [
    {
      id: "start",
      name: "start",
      category: "Basic",
      params: [{ name: "start", type: "int", value: 0 }],
    },
    {
      id: "end",
      name: "end",
      category: "Basic",
      params: [] as { name: string; type: "int" | "float"; value: 0 }[],
    },
  ];

  const [droppedBlocks, setDroppedBlocks] =
    useState<DroppedBlock[]>(initialBlocks);

  const handleDragEnd = (result: any) => {
    const { source, destination } = result;

    // 드롭이 유효한 위치에서 일어나지 않은 경우
    if (!destination) return;

    // 휴지통에 드래그 시 삭제
    if (destination.droppableId === "trash") {
      // start나 end 블록은 삭제하지 않음
      if (
        droppedBlocks[source.index].name === "start" ||
        droppedBlocks[source.index].name === "end"
      ) {
        return;
      }

      setDroppedBlocks((blocks) =>
        blocks.filter((_, index) => index !== source.index),
      );
      return;
    }

    // 왼쪽에서 오른쪽으로 드래그 시 추가
    if (
      source.droppableId.startsWith("left") &&
      destination.droppableId === "right"
    ) {
      const categoryIndex = parseInt(source.droppableId.split("-")[1], 10);
      const sourceCategory = categories[categoryIndex][0];
      const sourceBlocks = categories[categoryIndex][1];
      const blockToAdd = sourceBlocks[source.index];

      const newBlock: DroppedBlock = {
        ...blockToAdd,
        id: `${blockToAdd.name}-${Date.now()}`,
        category: sourceCategory,
      };

      setDroppedBlocks((blocks) => {
        const updatedBlocks = Array.from(blocks);
        // start와 end 블록 사이에만 추가되도록 제한
        const targetIndex = Math.max(
          1,
          Math.min(destination.index, updatedBlocks.length - 1),
        );
        updatedBlocks.splice(targetIndex, 0, newBlock);
        return updatedBlocks;
      });
      return; // 추가 후 즉시 리턴
    }

    // 드래그한 블록이 start 또는 end가 아닐 때만 순서 변경
    if (source.index > 0 && source.index < droppedBlocks.length - 1) {
      const updatedBlocks = Array.from(droppedBlocks);
      const [movedBlock] = updatedBlocks.splice(source.index, 1);

      // `destination.index`를 1과 마지막 index 사이로 제한
      const targetIndex = Math.max(
        1,
        Math.min(destination.index, updatedBlocks.length - 1),
      );
      updatedBlocks.splice(targetIndex, 0, movedBlock);

      setDroppedBlocks(updatedBlocks);
      return;
    }

    // 오른쪽에서 왼쪽으로 드래그 시 삭제
    if (
      source.droppableId === "right" &&
      destination.droppableId.startsWith("left")
    ) {
      setDroppedBlocks((prevBlocks) =>
        prevBlocks.filter((_, index) => index !== source.index),
      );
      return;
    }

    // 오른쪽 내에서 순서 변경
    if (source.droppableId === "right" && destination.droppableId === "right") {
      const reorderedBlocks = Array.from(droppedBlocks);
      const [movedBlock] = reorderedBlocks.splice(source.index, 1);
      reorderedBlocks.splice(destination.index, 0, movedBlock);

      setDroppedBlocks(reorderedBlocks);
      return;
    }
  };

  const { setBlockList } = useBlockStore();
  useEffect(() => {
    setBlockList(droppedBlocks);
  }, [droppedBlocks]);
  const handleBlur = (
    blockIndex: number,
    paramIndex: number,
    value: number,
  ) => {
    setDroppedBlocks((blocks) => {
      const updatedBlocks = [...blocks];
      const updatedBlock = { ...updatedBlocks[blockIndex] };
      updatedBlock.params = [...updatedBlock.params];
      updatedBlock.params[paramIndex] = {
        ...updatedBlock.params[paramIndex],
        value,
      };
      updatedBlocks[blockIndex] = updatedBlock;
      return updatedBlocks;
    });
  };
  return (
    <DragDropContext onDragEnd={handleDragEnd}>
      <div className="flex flex-1">
        <TabGroup as="div" className="flex">
          {/* 왼쪽 탭 버튼 */}
          <TabList className="flex flex-1 flex-col gap-4 border-r border-gray-500 bg-stone-200 p-4">
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
                  className={`h-[40px] w-[40px] rounded-full border-2 ${categoryColors[category]}`}
                ></div>
                {categoryDisplayNames[category]}
              </Tab>
            ))}
          </TabList>

          {/* 중앙 블록 리스트 */}
          <TabPanels className="flex border-r border-gray-500 bg-stone-200 p-20">
            {categories.map(([category, blocks], categoryIndex) => (
              <TabPanel key={category}>
                <Droppable
                  droppableId={`left-${categoryIndex}`}
                  isDropDisabled={true}
                >
                  {(provided) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                      className="flex flex-col gap-20"
                    >
                      {blocks.map((block, index) => (
                        <Draggable
                          key={`${block.name}-${category}`} // 고유한 key 사용
                          draggableId={`${block.name}-${category}`} // 고유한 draggableId 사용
                          index={index}
                        >
                          {(provided) => (
                            <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                            >
                              <BlockItem
                                block={block}
                                category={category}
                                small={true}
                              />
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </TabPanel>
            ))}
          </TabPanels>
        </TabGroup>

        {/* 오른쪽 드롭 영역 */}
        <Droppable droppableId="right">
          {(provided) => (
            <div
              ref={provided.innerRef}
              {...provided.droppableProps}
              className="flex max-h-[92vh] flex-1 flex-col items-center gap-4 overflow-y-scroll bg-stone-300 p-20"
            >
              {droppedBlocks.map((block, index) => (
                <Draggable
                  key={block.id}
                  draggableId={block.id}
                  index={index}
                  isDragDisabled={
                    block.name === "start" || block.name === "end"
                  }
                >
                  {(provided) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                      {...provided.dragHandleProps}
                    >
                      <BlockItem
                        block={block}
                        category={block.category}
                        onBlurParam={(paramIndex, value) =>
                          handleBlur(index, paramIndex, value)
                        }
                      />
                    </div>
                  )}
                </Draggable>
              ))}
              {provided.placeholder}
            </div>
          )}
        </Droppable>

        {/* 왼쪽 하단 휴지통 Droppable */}
        <Droppable droppableId="trash">
          {(provided) => (
            <div
              ref={provided.innerRef}
              {...provided.droppableProps}
              className="fixed bottom-10 left-10 flex h-[50px] w-[50px] items-center justify-center rounded-full border border-gray-400 bg-red-200"
            >
              🗑️
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </div>
    </DragDropContext>
  );
};

export default BlockList;
