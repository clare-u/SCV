// components/BlockList.tsx
import { CustomBlockList } from "./CustomBlockList";
import { BlockDefinition, BlockCategory } from "@/types";
import BlockItem from "./BlockItem";

const BlockList: React.FC = () => {
  return (
    <div className="flex flex-col">
      {(
        Object.entries(CustomBlockList) as [BlockCategory, BlockDefinition[]][]
      ).map(([category, blocks]) => (
        <div className="flex flex-col gap-10" key={category}>
          <h2 className="mb-2 text-lg font-semibold">{category}</h2>
          {blocks.map((block) => (
            <BlockItem key={block.name} block={block} category={category} />
          ))}
        </div>
      ))}
    </div>
  );
};

export default BlockList;
