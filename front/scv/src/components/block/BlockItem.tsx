"use client";
import { useState } from "react";
import { BlockDefinition, BlockCategory } from "@/types";
import { toast } from "sonner";

interface BlockItemProps {
  block: BlockDefinition;
  category: BlockCategory;
  small?: boolean;
  open?: boolean;
  onBlurParam?: (paramIndex: number, value: number) => void;
  isEditable?: boolean;
}

const categoryColors = {
  Basic: {
    bg: "bg-gray-700",
    openBg: "bg-gray-500",
    border: "border-gray-700",
  },
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
  open = false,
  onBlurParam,
  isEditable = true,
}) => {
  const [isOpen, setIsOpen] = useState(open);

  const toggleOpen = () => {
    if (!small && isEditable) {
      setIsOpen(!isOpen);
    }
  };

  const colorClasses = categoryColors[category];
  const widthClass = small ? "w-[300px]" : "w-[400px]";

  return (
    <div className={`transition-all duration-300 ease-in-out ${widthClass}`}>
      <div
        className={`flex w-full cursor-pointer ${colorClasses.bg} items-center justify-center p-10 ${
          isOpen && block.params.length > 0 ? "rounded-t-12" : "rounded-12"
        } transition-all duration-300 ease-in-out ${
          isOpen ? "shadow-lg" : "shadow-md hover:shadow-lg"
        } `}
        onClick={toggleOpen}
      >
        <div className="text-20 font-extrabold text-white">{block.name}</div>
      </div>

      <div
        className={`overflow-hidden transition-all duration-300 ease-in-out ${
          isOpen ? "max-h-[500px] opacity-100" : "max-h-0 opacity-0"
        }`}
      >
        {block.params.length > 0 && (
          <div
            className={`flex flex-col items-center justify-center rounded-b-12 border-2 p-20 ${colorClasses.openBg} ${
              colorClasses.border
            } transition-all duration-300 ease-in-out ${
              isOpen ? "translate-y-0 transform" : "-translate-y-4 transform"
            }`}
          >
            <ul className="flex flex-col items-center justify-center gap-10">
              {block.params.map((param, idx) => (
                <li key={param.name} className="mb-2 flex gap-10">
                  <label className="font-semibold text-white">
                    {param.name}
                  </label>
                  <input
                    type="number"
                    className={`appearance-none rounded-[20px] border ${colorClasses.border} w-[60px] bg-white p-2 text-center text-sm placeholder-gray-500 transition-all duration-200 ease-in-out focus:shadow-md focus:ring-2 focus:ring-opacity-50 ${colorClasses.border.replace(
                      "border",
                      "ring",
                    )}`}
                    onBlur={(e) => {
                      if (!isEditable || block.name === "start") return;

                      if (e.target.value.trim().length === 0) {
                        e.target.value = "";
                        param.value = undefined;
                        return;
                      }

                      const value = parseFloat(e.target.value);
                      param.value = value;

                      if (param.min !== undefined && value < param.min) {
                        toast.error(`최솟값은 ${param.min}입니다.`);
                        e.target.value = String(param.min);
                        param.value = param.min;
                      }
                      if (param.max !== undefined && value > param.max) {
                        toast.error(`최댓값은 ${param.max}입니다.`);
                        e.target.value = String(param.max);
                        param.value = param.max;
                      }
                      if (param.value !== undefined && onBlurParam) {
                        onBlurParam(idx, param.value); // 파라미터 값 업데이트
                      }
                    }}
                    readOnly={!isEditable || block.name === "start"}
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
