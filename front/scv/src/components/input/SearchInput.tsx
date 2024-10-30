import { Input } from "@headlessui/react";
import { useState } from "react";

interface SearchInputProps {
  placeholder: string;
  onSubmit: (value: string) => void;
}

const SearchInput: React.FC<SearchInputProps> = ({ placeholder, onSubmit }) => {
  const [value, setValue] = useState<string>("");

  const clearInput = () => setValue("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value);
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter" && value.trim()) {
      onSubmit(value.trim()); // Enter 키를 눌렀을 때 검색어가 있을 경우만 전달
    }
  };

  const handleSearchClick = () => {
    if (value.trim()) {
      onSubmit(value.trim()); // 검색어가 있을 경우만 실행
    }
  };

  return (
    <div className="flex w-full items-center gap-10 rounded-12 border border-gray-400 p-10">
      <span
        className="material-symbols-outlined cursor-pointer text-gray-400 hover:text-gray-200"
        onClick={handleSearchClick}
      >
        search
      </span>
      <Input
        className="w-full outline-none"
        placeholder={placeholder}
        type="text"
        name={placeholder}
        value={value}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
      />
      {value && (
        <span
          className="material-symbols-outlined cursor-pointer text-gray-400 hover:text-gray-200"
          onClick={clearInput}
        >
          cancel
        </span>
      )}
    </div>
  );
};

export default SearchInput;
