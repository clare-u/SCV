import { Dataset } from "@/types";
import { create } from "zustand";
import { BlockDefinition } from "@/types";
import { toast } from "sonner";

// 데이터셋별 레이블 개수 매핑
const datasetLabels: Record<Dataset, number> = {
  MNIST: 10,
  Fashion: 10,
  "CIFAR-10": 10,
  SVHN: 10,
  EMNIST: 26,
};

// 데이터셋별 채널 매핑
const datasetChannels: Record<Dataset, number> = {
  MNIST: 1,
  Fashion: 1,
  "CIFAR-10": 3,
  SVHN: 3,
  EMNIST: 1,
};

// 데이터셋별 size 매핑
const datasetSizes: Record<Dataset, number> = {
  MNIST: 28,
  Fashion: 28,
  "CIFAR-10": 32,
  SVHN: 32,
  EMNIST: 28,
};

interface BlockState {
  blockList: BlockDefinition[] | null;
  setBlockList: (blockList: BlockDefinition[]) => void;
  blockListValidation: (dataset: Dataset) => void;
}

interface tensorShape {
  channels: number;
  height: number;
  width: number;
}

const validateBlock = (
  input: tensorShape | undefined,
  block: BlockDefinition,
) => {
  if (input === undefined) return;

  var out_channels = input.channels;

  if (block.name === "nn.Conv2d") {
    const in_channels = block.params[0].value;

    if (input.channels != in_channels) {
      toast.error(
        `Conv2d의 input_channel : ${in_channels} 과 이전 레이어의 output_channel : ${input.channels}이 맞지 않습니다.`,
      );
      return;
    }

    out_channels = block.params[1].value;
    const kernel_size = block.params[2].value;

    if (kernel_size >= input.width || kernel_size >= input.height) {
      toast.error(
        `Conv2d의 kernel_size : ${kernel_size}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
      return;
    }

    return {
      channels: out_channels,
      height: Math.floor(input.height - kernel_size - 1 - 1 + 1),
      width: Math.floor(input.width - kernel_size - 1 - 1 + 1),
    };
  }
  if (block.name === "nn.ConvTranspose2d") {
    const in_channels = block.params[0].value;

    if (input.channels != in_channels) {
      toast.error(
        `ConvTranspose2d의 input_channel : ${in_channels} 과 이전 레이어의 output_channel : ${input.channels}이 맞지 않습니다.`,
      );
      return;
    }

    out_channels = block.params[1].value;
    const kernel_size = block.params[2].value;

    if (kernel_size >= input.width || kernel_size >= input.height) {
      toast.error(
        `ConvTranspose2d의 kernel_size : ${kernel_size}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
      return;
    }

    return {
      channels: out_channels,
      height: input.height - 1 + kernel_size - 1 + 1,
      width: input.width - 1 + kernel_size - 1 + 1,
    };
  }
  if (["MaxPool2d", "AvgPool2d"].includes(block.name)) {
    const kernel_size = block.params[0].value;
    const stride = block.params[1].value;
    const constant = block.name === "MaxPool2d" ? 1 : 0;

    if (kernel_size >= input.width || kernel_size >= input.height) {
      toast.error(
        `${block.name}의 kernel_size : ${kernel_size}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
      return;
    }

    if (stride >= input.width || stride >= input.height) {
      toast.error(
        `${block.name}의 stride : ${stride}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
    }

    return {
      channels: out_channels,
      height: Math.floor(
        (input.height - (kernel_size - constant) - constant) / stride + 1,
      ),
      width: Math.floor(
        (input.width - (kernel_size - constant) - constant) / stride + 1,
      ),
    };
  }
  if (
    [
      "ReflectionPad2d",
      "ReplicationPad2d",
      "ZeroPad2d",
      "ConstantPad2d",
    ].includes(block.name)
  ) {
    const padding = block.params[0].value;
    return {
      channels: out_channels,
      height: input.height + padding + padding,
      width: input.width + padding + padding,
    };
  }
  if (block.name === "Linear") {
    const in_channels = block.params[0].value;

    if (input.channels != in_channels) {
      toast.error(
        `${block.name}의 input_channel : ${in_channels} 과 이전 레이어의 output_channel : ${input.channels}이 맞지 않습니다.`,
      );
      return;
    }

    out_channels = block.params[1].value;
    return {
      channels: out_channels,
      height: 1,
      width: 1,
    };
  }
  return input;
};

export const useBlockStore = create<BlockState>((set, get) => ({
  blockList: null,
  setBlockList: (blockList: BlockDefinition[]) => {
    // 각 블록의 각 param에 기본 value 속성을 추가
    const updatedBlockList = blockList.map((block) => ({
      ...block,
      params: block.params
        .filter((param) => param.name && param.type) // name과 type이 있는 항목만 필터링
        .map((param) => ({
          ...param,
          value: param.value !== undefined ? param.value : 0, // value가 없으면 기본값 0 설정
        })),
    }));

    set({ blockList: updatedBlockList });
  },
  blockListValidation: (dataset: Dataset) => {
    // input의 channels 와 레이어의 in_channels 를 검사
    // input의 width, height와 레이어의 kernel_size 를 검사
    const blockList = get().blockList;
    let dataShape: tensorShape | undefined = {
      channels: datasetChannels[dataset],
      width: datasetSizes[dataset],
      height: datasetSizes[dataset],
    };
    blockList?.map((block) => {
      console.log(dataShape);
      if (!dataShape) return;
      dataShape = validateBlock(dataShape, block);
    });

    console.log(dataShape);
    if (dataShape && dataShape.channels !== datasetLabels[dataset]) {
      toast.error(
        `마지막 출력은 ${datasetLabels[dataset]} 개의 channel로 이루어져 있어야 합니다.`,
      );
    }
  },
}));
