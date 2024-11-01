import { Menu, MenuButton, MenuItem, MenuItems } from "@headlessui/react";

interface FilterDropdownProps {
  selectedFilter: string;
  onSelectFilter: (filter: string) => void;
  filterOptions: string[]; // 필터 옵션 리스트 추가
}

const FilterDropdown: React.FC<FilterDropdownProps> = ({
  selectedFilter,
  onSelectFilter,
  filterOptions,
}) => {
  return (
    <div className="relative inline-block text-left">
      <Menu>
        <MenuButton className="inline-flex h-[40px] w-[95px] items-center justify-between rounded-md border border-gray-300 bg-white px-10 py-4 text-14 font-medium text-gray-700 shadow-sm hover:bg-gray-50">
          {selectedFilter}
          <span className="material-symbols-outlined text-gray-400">
            keyboard_arrow_down
          </span>
        </MenuButton>
        <MenuItems className="absolute right-0 mt-2 w-[95px] origin-top-right rounded-md border border-gray-200 bg-white p-10 shadow-lg outline-none">
          {filterOptions.map((option) => (
            <MenuItem key={option}>
              {({ active }) => (
                <button
                  className={`${
                    active ? "bg-gray-400 text-white" : "text-gray-900"
                  } group flex w-full items-center rounded-md p-4 text-sm`}
                  onClick={() => onSelectFilter(option)}
                >
                  {option}
                </button>
              )}
            </MenuItem>
          ))}
        </MenuItems>
      </Menu>
    </div>
  );
};

export default FilterDropdown;
