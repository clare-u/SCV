import { Input } from "@headlessui/react";

interface SearchInputProps {
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (value: string) => void;
}

const SearchInput: React.FC<SearchInputProps> = ({
  placeholder,
  value,
  onChange,
  onSubmit,
}) => {
  const clearInput = () =>
    onChange({ target: { value: "" } } as React.ChangeEvent<HTMLInputElement>);

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      onSubmit(value);
    }
  };

  const handleSearchClick = () => {
    onSubmit(value);
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
        onChange={onChange}
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
