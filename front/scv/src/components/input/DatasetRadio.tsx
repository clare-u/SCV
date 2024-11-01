import { RadioGroup, Radio } from "@headlessui/react";
import Chips from "../chips/Chips";
import { ChipsProps } from "../chips/Chips"; // ChipsProps import

interface DatasetRadioProps {
  options: string[];
  selected: string;
  onChange: (value: string) => void;
}

const colorMapping: Record<string, ChipsProps["color"]> = {
  전체: "black",
  MNIST: "indigo",
  Fashion: "amber",
  "CIFAR-10": "green",
  SVHN: "teal",
  EMNIST: "red",
};

const DatasetRadio: React.FC<DatasetRadioProps> = ({
  options,
  selected,
  onChange,
}) => {
  return (
    <RadioGroup
      value={selected}
      onChange={onChange}
      className="flex gap-[15px] py-10"
    >
      {options.map((option) => (
        <Radio key={option} value={option}>
          {({ checked }) => (
            <Chips
              color={colorMapping[option]}
              design={checked ? "fill" : "outline"}
            >
              {option}
            </Chips>
          )}
        </Radio>
      ))}
    </RadioGroup>
  );
};

export default DatasetRadio;
