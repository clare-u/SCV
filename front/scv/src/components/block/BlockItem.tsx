"use client";
import { useState } from "react";
import { BlockDefinition, BlockCategory } from "@/types";

interface BlockItemProps {
  block: BlockDefinition;
  category: BlockCategory;
  small?: boolean;
}

const categoryColors = {
  Convolution: {
    bg: "bg-blue-700",
    openBg: "bg-blue-500",
    border: "border-blue-700",
  },
  Pooling: {
    bg: "bg-violet-700",
    openBg: "bg-violet-500",
    border: "border-violet-700",
  },
  Padding: {
    bg: "bg-yellow-700",
    openBg: "bg-yellow-500",
    border: "border-yellow-700",
  },
  Activation: {
    bg: "bg-green-700",
    openBg: "bg-green-500",
    border: "border-green-700",
  },
  Linear: {
    bg: "bg-red-700",
    openBg: "bg-red-500",
    border: "border-red-700",
  },
};

const BlockItem: React.FC<BlockItemProps> = ({
  block,
  category,
  small = false,
}) => {
  const [isOpen, setIsOpen] = useState(false);

  const toggleOpen = () => {
    if (!small) {
      setIsOpen(!isOpen);
    }
  };

  const colorClasses = categoryColors[category];
  const widthClass = small ? "w-[300px]" : "w-[400px]";

  return (
    <div className={`transition-all duration-300 ease-in-out ${widthClass}`}>
      <div
        className={`flex w-full cursor-pointer ${colorClasses.bg} items-center justify-center p-10 ${isOpen && block.params.length > 0 ? "rounded-t-12" : "rounded-12"} transition-all duration-300 ease-in-out ${isOpen ? "shadow-lg" : "shadow-md hover:shadow-lg"}`}
        onClick={toggleOpen}
      >
        <div className="text-20 font-extrabold text-white">{block.name}</div>
      </div>

      <div
        className={`overflow-hidden transition-all duration-300 ease-in-out ${isOpen ? "max-h-[500px] opacity-100" : "max-h-0 opacity-0"} `}
      >
        {block.params.length > 0 && (
          <div
            className={`flex flex-col items-center justify-center rounded-b-12 border-2 p-20 ${colorClasses.openBg} ${colorClasses.border} transition-all duration-300 ease-in-out ${isOpen ? "translate-y-0 transform" : "-translate-y-4 transform"} `}
          >
            <ul className="flex flex-col items-center justify-center gap-10">
              {block.params.map((param) => (
                <li key={param.name} className="mb-2 flex gap-10">
                  <label className="font-semibold text-white">
                    {param.name}
                  </label>
                  <input
                    type="text"
                    className={`rounded-[20px] border ${colorClasses.border} w-[60px] bg-white p-2 text-center text-sm placeholder-gray-500 transition-all duration-200 ease-in-out focus:shadow-md focus:ring-2 focus:ring-opacity-50 ${colorClasses.border.replace("border", "ring")}`}
                    placeholder={
                      param.type === "int"
                        ? "0"
                        : param.type === "float"
                          ? "0.0"
                          : ""
                    }
                  />
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default BlockItem;
