import { BlockCategory, BlockParam, BlockDefinition } from "@/types";
import { CustomBlockList } from "@/components/block/CustomBlockList";

interface BackendLayer {
  name: string;
  [key: string]: any;
}

interface FrontendBlock {
  category: BlockCategory;
  name: string;
  params: BlockParam[];
}

// 백엔드 레이어 이름을 프론트엔드 블록 이름으로 매핑
const blockNameMapping: Record<string, string> = {
  Conv2d: "nn.Conv2d",
  ConvTranspose2d: "nn.ConvTranspose2d",
  MaxPool2d: "MaxPool2d",
  AvgPool2d: "AvgPool2d",
  ReflectionPad2d: "ReflectionPad2d",
  ReplicationPad2d: "ReplicationPad2d",
  ZeroPad2d: "ZeroPad2d",
  ConstantPad2d: "ConstantPad2d",
  ReLU: "ReLU",
  LeakyReLU: "LeakyReLU",
  ELU: "ELU",
  PReLU: "PReLU",
  Sigmoid: "Sigmoid",
  Tanh: "Tanh",
  Softmax: "Softmax",
  LogSoftmax: "LogSoftmax",
  GELU: "GELU",
  Linear: "Linear",
};

// 렌더링하지 않을 레이어 이름들
const excludedLayers = ["Flatten"];

// 프론트엔드 블록 데이터를 백엔드 API 형식으로 변환
export function convertBlocksToApiFormat(blocks: FrontendBlock[]): {
  layers: BackendLayer[];
} {
  const layers = blocks.map((block) => {
    const name = block.name.replace("nn.", "");

    if (!block.params || block.params.length === 0) {
      return { name };
    }

    const layer: BackendLayer = { name };
    block.params.forEach((param) => {
      if (param.value !== undefined) {
        layer[param.name] = param.value;
      }
    });

    return layer;
  });

  return { layers };
}

// 백엔드 API 응답을 프론트엔드 블록 형식으로 변환
export function convertApiToBlocks(apiData: {
  layers: BackendLayer[];
}): FrontendBlock[] {
  // 제외할 레이어 필터링
  const filteredLayers = apiData.layers.filter(
    (layer) => !excludedLayers.includes(layer.name),
  );

  return filteredLayers.map((layer) => {
    // 매핑된 이름 또는 원래 이름 사용
    const mappedName = blockNameMapping[layer.name] || layer.name;

    // CustomBlockList에서 해당하는 블록 정의 찾기
    const blockDef = findBlockDefinition(mappedName);
    if (!blockDef) {
      throw new Error(`Unknown block type: ${mappedName}`);
    }

    // 파라미터 값 설정
    const params = blockDef.params.map((param) => ({
      ...param,
      value: layer[param.name],
    }));

    return {
      category: findBlockCategory(mappedName),
      name: blockDef.name,
      params,
    };
  });
}

function findBlockDefinition(name: string): BlockDefinition | undefined {
  const normalizedName = name.includes("nn.") ? name : name;

  for (const category of Object.keys(CustomBlockList) as BlockCategory[]) {
    const found = CustomBlockList[category].find(
      (block) => block.name === normalizedName,
    );
    if (found) return found;
  }
  return undefined;
}

function findBlockCategory(name: string): BlockCategory {
  for (const category of Object.keys(CustomBlockList) as BlockCategory[]) {
    if (CustomBlockList[category].some((block) => block.name === name)) {
      return category;
    }
  }
  throw new Error(`Cannot find category for block: ${name}`);
}
