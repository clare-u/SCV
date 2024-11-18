"use client";
import { useState } from "react";
import { BlockDefinition, BlockCategory } from "@/types";
import { toast } from "sonner";
import Tippy from "@tippyjs/react";
import "tippy.js/dist/tippy.css";

interface BlockItemProps {
  block: BlockDefinition;
  category: BlockCategory;
  small?: boolean;
  open?: boolean;
  onClick?: () => void;
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
  onClick,
  onBlurParam,
  isEditable = true,
}) => {
  const [isOpen, setIsOpen] = useState(open);
  const [paramValues, setParamValues] = useState<Record<string, number>>(() => {
    // Initialize with existing values from block.params
    const initialValues: Record<string, number> = {};
    block.params.forEach((param) => {
      initialValues[param.name] = param.value ?? 0; // Use 0 as default if value is undefined
    });
    return initialValues;
  });

  const toggleOpen = (e: React.MouseEvent) => {
    if (!small && isEditable) {
      // 도움말 아이콘을 클릭했을 때는 토글하지 않음
      if (!(e.target as HTMLElement).closest(".help-icon")) {
        setIsOpen(!isOpen);
      }
    }
  };

  const colorClasses = categoryColors[category];
  const widthClass = small ? "w-[300px]" : "w-[400px]";

  const handleBlur = (
    paramName: string,
    paramIndex: number,
    e: React.FocusEvent<HTMLInputElement>,
  ) => {
    if (!isEditable || block.name === "start") return;

    if (e.target.value.trim().length === 0) {
      setParamValues((prev) => ({ ...prev, [paramName]: 0 }));
      return;
    }

    const value = parseFloat(e.target.value);
    const param = block.params[paramIndex];

    let finalValue = value;
    if (param.min !== undefined && value < param.min) {
      toast.error(`최솟값은 ${param.min}입니다.`);
      finalValue = param.min;
    }
    if (param.max !== undefined && value > param.max) {
      toast.error(`최댓값은 ${param.max}입니다.`);
      finalValue = param.max;
    }

    setParamValues((prev) => ({ ...prev, [paramName]: finalValue }));
    if (onBlurParam) {
      onBlurParam(paramIndex, finalValue);
    }
  };

  console.log("Block tooltip:", block.tooltip); // 디버깅을 위한 로그 추가

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
        <div className="flex items-center gap-10 text-20 font-extrabold text-white">
          {block.name}
          {block.tooltip && (
            <Tippy content={block.tooltip} interactive={true}>
              <span
                className="material-symbols-outlined help-icon cursor-pointer"
                onClick={(e) => e.stopPropagation()}
              >
                help
              </span>
            </Tippy>
          )}
        </div>
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
                    value={paramValues[param.name]}
                    onChange={(e) => {
                      const value =
                        e.target.value === "" ? 0 : parseFloat(e.target.value);
                      setParamValues((prev) => ({
                        ...prev,
                        [param.name]: value,
                      }));
                    }}
                    className={`appearance-none rounded-[20px] border ${colorClasses.border} w-[60px] bg-white p-2 text-center text-sm placeholder-gray-500 transition-all duration-200 ease-in-out focus:shadow-md focus:ring-2 focus:ring-opacity-50 ${colorClasses.border.replace(
                      "border",
                      "ring",
                    )}`}
                    onBlur={(e) => handleBlur(param.name, idx, e)}
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
