import {
  Listbox,
  ListboxButton,
  ListboxOption,
  ListboxOptions,
} from "@headlessui/react";

interface ListboxOptionType {
  id: number;
  name: string;
}

interface ListboxComponentProps {
  value: ListboxOptionType;
  onChange: (value: ListboxOptionType) => void;
  options: ListboxOptionType[];
  color?: "dark" | "light"; // color prop 추가
}

const ListboxComponent: React.FC<ListboxComponentProps> = ({
  value,
  onChange,
  options,
  color = "light", // 기본값을 light로 설정
}) => {
  return (
    <Listbox value={value} onChange={onChange}>
      <ListboxButton
        className={`flex w-full items-center justify-between rounded-12 border border-gray-400 p-10 outline-none data-[focus]:border-2 data-[focus]:border-gray-400 data-[hover]:shadow ${
          color === "dark" ? "bg-indigo-900 text-white" : ""
        }`}
      >
        {value.name}
        <span
          className={`material-symbols-outlined ${color === "dark" ? "text-gray-300" : "text-gray-400"}`}
        >
          keyboard_arrow_down
        </span>
      </ListboxButton>
      <ListboxOptions
        anchor="bottom"
        transition
        className={`flex w-[var(--button-width)] flex-col gap-10 rounded-12 border border-gray-400 p-10 transition duration-100 ease-in [--anchor-gap:var(--spacing-1)] focus:outline-none data-[leave]:data-[closed]:opacity-0 ${
          color === "dark"
            ? "bg-indigo-900 text-white"
            : "bg-white text-gray-900"
        }`}
      >
        {options.map((option) => (
          <ListboxOption
            key={option.id}
            value={option}
            className={`cursor-pointer rounded-10 p-4 ${
              color === "dark"
                ? "data-[focus]:bg-indigo-800"
                : "data-[focus]:bg-gray-200"
            }`}
          >
            {option.name}
          </ListboxOption>
        ))}
      </ListboxOptions>
    </Listbox>
  );
};

export default ListboxComponent;
