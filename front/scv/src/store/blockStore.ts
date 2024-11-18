import { Dataset } from "@/types";
import { create } from "zustand";
import { BlockDefinition, Layer } from "@/types";
import { toast } from "sonner";
import { convertBlocksToApiFormat } from "@/utils/block-converter";

// 데이터셋별 레이블 개수 매핑
export const datasetLabels: Record<Dataset, number> = {
  MNIST: 10,
  Fashion: 10,
  CIFAR10: 10,
  SVHN: 10,
  EMNIST: 26,
};

// 데이터셋별 채널 매핑
export const datasetChannels: Record<Dataset, number> = {
  MNIST: 1,
  Fashion: 1,
  CIFAR10: 3,
  SVHN: 3,
  EMNIST: 1,
};

// 데이터셋별 크기 매핑
export const datasetSizes: Record<Dataset, number> = {
  MNIST: 28,
  Fashion: 28,
  CIFAR10: 32,
  SVHN: 32,
  EMNIST: 28,
};

// 텐서 형상 정의
interface tensorShape {
  channels: number;
  height: number;
  width: number;
}

// 상태 관리 인터페이스 정의
interface BlockState {
  blockList: BlockDefinition[] | null;
  setBlockList: (blockList: BlockDefinition[]) => void;
  blockListValidation: (dataset: Dataset) => boolean;
  getLayerData: () => Layer[]; // 레이어 데이터를 가져오는 함수
}

// 블록 유효성 검사 함수
const validateBlock = (
  input: tensorShape | undefined,
  block: BlockDefinition,
) => {
  if (input === undefined) return undefined;
  var out_channels: number = input.channels;

  // 파라미터 검증
  const hasEmptyParams = block.params.some((param) => {
    if (param.value === undefined) {
      toast.error(
        `${block.name} 블록의 ${param.name} 파라미터에 빈칸이 있습니다.`,
      );
      return true;
    }
    return false;
  });

  if (hasEmptyParams) return undefined;

  if (block.name === "nn.Conv2d") {
    const in_channels = block.params[0].value;

    if (input.channels != in_channels) {
      toast.error(
        `Conv2d의 input_channel : ${in_channels} 과 이전 레이어의 output_channel : ${input.channels}이 맞지 않습니다.`,
      );
      return undefined;
    }

    if (block.params[1].value) {
      out_channels = block.params[1].value;
    }
    const kernel_size = block.params[2].value;

    if (kernel_size === undefined) return undefined;

    if (kernel_size >= input.width || kernel_size >= input.height) {
      toast.error(
        `Conv2d의 kernel_size : ${kernel_size}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
      return undefined;
    }

    return {
      channels: out_channels,
      height: Math.floor(input.height - (kernel_size - 1) - 1 + 1),
      width: Math.floor(input.width - (kernel_size - 1) - 1 + 1),
    };
  }
  if (block.name === "nn.ConvTranspose2d") {
    const in_channels = block.params[0].value;

    if (input.channels != in_channels) {
      toast.error(
        `ConvTranspose2d의 input_channel : ${in_channels} 과 이전 레이어의 output_channel : ${input.channels}이 맞지 않습니다.`,
      );
      return undefined;
    }
    if (block.params[1].value) {
      out_channels = block.params[1].value;
    }
    const kernel_size = block.params[2].value;

    if (kernel_size === undefined) return undefined;

    if (kernel_size >= input.width || kernel_size >= input.height) {
      toast.error(
        `ConvTranspose2d의 kernel_size : ${kernel_size}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
      return undefined;
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

    if (kernel_size === undefined) return undefined;

    if (kernel_size >= input.width || kernel_size >= input.height) {
      toast.error(
        `${block.name}의 kernel_size : ${kernel_size}이 input data size : ${input.width} * ${input.height} 보다 큽니다.`,
      );
      return undefined;
    }

    if (stride === undefined) return undefined;

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
    if (padding === undefined) return undefined;

    return {
      channels: out_channels,
      height: input.height + padding + padding,
      width: input.width + padding + padding,
    };
  }
  if (block.name === "Linear") {
    const in_channels = block.params[0].value;

    if (input.channels * input.height * input.width != in_channels) {
      toast.error(
        `${block.name}의 input_channel : ${in_channels} 과 이전 레이어의 output_channel : ${input.channels * input.height * input.width}이 맞지 않습니다.`,
      );
      return undefined;
    }
    if (block.params[1].value) {
      out_channels = block.params[1].value;
    }
    return {
      channels: out_channels,
      height: 1,
      width: 1,
    };
  }
  return input;
};

// Zustand 스토어 정의
export const useBlockStore = create<BlockState>((set, get) => ({
  blockList: null,

  setBlockList: (blockList: BlockDefinition[]) => {
    const updatedBlockList = blockList.map((block) => ({
      ...block,
      params: block.params.map((param) => ({
        ...param,
        value: param.value,
      })),
    }));
    set({ blockList: updatedBlockList });
  },

  blockListValidation: (dataset: Dataset): boolean => {
    // input의 channels 와 레이어의 in_channels 를 검사
    // input의 width, height와 레이어의 kernel_size 를 검사
    const blockList = get().blockList;
    let dataShape: tensorShape | undefined = {
      channels: datasetChannels[dataset],
      width: datasetSizes[dataset],
      height: datasetSizes[dataset],
    };

    blockList?.forEach((block) => {
      if (block.name !== "start" && block.name !== "end") {
        dataShape = validateBlock(dataShape, block);
      }
    });

    if (!dataShape) {
      return false; // validation 실패 시 즉시 false 반환
    }

    if (dataShape && dataShape.channels !== datasetLabels[dataset]) {
      toast.error(
        `마지막 출력은 ${datasetLabels[dataset]} 개의 channel로 이루어져 있어야 합니다.`,
      );
      return false;
    }
    if (dataShape && (dataShape.width != 1 || dataShape.height != 1)) {
      toast.error(
        `마지막 출력 전에 width와 height을 1로 만들어 주세요. (힌트: Linear 레이어)`,
      );
      return false;
    }

    return true;
  },

  getLayerData: () => {
    const blockList = get().blockList;
    if (!blockList) return [];

    // convertBlocksToApiFormat 함수를 사용하여 변환
    const { layers } = convertBlocksToApiFormat(blockList);
    return layers;
  },
}));
