import { Input } from "@headlessui/react";
import { ChangeEvent } from "react";

interface ModalInputProps {
  placeholder: string;
  value: string;
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
  color?: "dark" | "light"; // color prop 추가
}

const ModalInput: React.FC<ModalInputProps> = ({
  placeholder,
  value,
  onChange,
  color = "light", // 기본값을 light로 설정
}) => {
  return (
    <Input
      className={`h-[42px] w-full rounded-12 border border-gray-400 p-10 placeholder-gray-400 outline-none data-[focus]:border-2 data-[focus]:border-gray-400 data-[hover]:shadow ${
        color === "dark" ? "bg-indigo-900 text-white" : ""
      }`}
      placeholder={placeholder}
      type="text"
      value={value}
      onChange={onChange}
    />
  );
};

export default ModalInput;
