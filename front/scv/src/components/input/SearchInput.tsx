import { Input } from "@headlessui/react";
import { useState } from "react";

interface SearchInputProps {
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleKeyDown: (event: React.KeyboardEvent<HTMLInputElement>) => void;
}

const SearchInput: React.FC<SearchInputProps> = ({
  placeholder,
  value,
  onChange,
  handleKeyDown,
}) => {
  // 입력 내용을 지우는 함수
  const clearInput = () => {
    onChange({ target: { value: "" } } as React.ChangeEvent<HTMLInputElement>);
  };

  return (
    <div className="flex w-full items-center gap-10 rounded-lg border border-gray-400 p-10">
      <span className="material-symbols-outlined text-gray-400">search</span>
      <Input
        className="w-full outline-none"
        placeholder={placeholder}
        type="text"
        name={placeholder}
        value={value}
        onChange={onChange}
        onKeyDown={handleKeyDown}
      />
      {value && (
        <span
          className="material-symbols-outlined cursor-pointer text-gray-400"
          onClick={clearInput}
        >
          close
        </span>
      )}
    </div>
  );
};

export default SearchInput;
