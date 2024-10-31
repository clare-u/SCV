"use client";
import { useState } from "react";
import { Tab, TabPanel, TabGroup, TabList, TabPanels } from "@headlessui/react";
import { DragDropContext, Draggable, Droppable } from "@hello-pangea/dnd";
import { CustomBlockList } from "./CustomBlockList";
import { BlockDefinition, BlockCategory } from "@/types";
import BlockItem from "./BlockItem";

// 카테고리 표시 이름 매핑
const categoryDisplayNames: Record<BlockCategory, string> = {
  Convolution: "Convolution Layers",
  Pooling: "Pooling Layers",
  Padding: "Padding Layers",
  Activation: "Non-Linear Activations",
  Linear: "Linear Layers",
};

// 카테고리별 색상 스타일 매핑
const categoryColors: Record<BlockCategory, string> = {
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
  const categories = Object.entries(CustomBlockList) as [
    BlockCategory,
    BlockDefinition[],
  ][];
  const [droppedBlocks, setDroppedBlocks] = useState<DroppedBlock[]>([]);

  const handleDragEnd = (result: any) => {
    const { source, destination } = result;

    // 드롭이 유효한 위치에서 일어나지 않은 경우
    if (!destination) return;

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

    // 휴지통에 드래그 시 삭제
    if (destination.droppableId === "trash") {
      setDroppedBlocks((blocks) =>
        blocks.filter((_, index) => index !== source.index),
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

    // 왼쪽에서 오른쪽으로 드래그 시 추가 (드롭한 위치에 맞게 삽입)
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

      // 새로운 블록을 드롭한 위치의 인덱스에 추가
      setDroppedBlocks((blocks) => {
        const updatedBlocks = Array.from(blocks);
        updatedBlocks.splice(destination.index, 0, newBlock);
        return updatedBlocks;
      });
    }
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
                <Draggable key={block.id} draggableId={block.id} index={index}>
                  {(provided) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                      {...provided.dragHandleProps}
                    >
                      <BlockItem block={block} category={block.category} />
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
