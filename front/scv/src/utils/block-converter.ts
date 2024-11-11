import { BlockCategory, BlockParam, BlockDefinition } from "@/types";
import { CustomBlockList } from "@/components/block/CustomBlockList";

// 백엔드 레이어 타입 정의
interface BackendLayer {
  name: string;
  [key: string]: any;
}

// 프론트엔드 블록 타입 정의
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

// 블록 정의를 찾는 함수
function findBlockDefinition(name: string): BlockDefinition | undefined {
  // nn. 접두사가 있는 경우와 없는 경우 모두 처리
  const searchName = name.startsWith("nn.") ? name : name;

  // 각 카테고리를 순회하면서 블록 정의를 찾음
  for (const category of Object.keys(CustomBlockList) as BlockCategory[]) {
    const found = CustomBlockList[category].find(
      (block) => block.name === searchName || block.name === `nn.${searchName}`,
    );
    if (found) return found;
  }

  // 블록 정의를 찾지 못한 경우 로그 출력
  console.warn(`Block definition not found for: ${name}`);
  return undefined;
}

// 블록의 카테고리를 찾는 함수
function findBlockCategory(name: string): BlockCategory {
  for (const category of Object.keys(CustomBlockList) as BlockCategory[]) {
    if (
      CustomBlockList[category].some(
        (block) => block.name === name || block.name === `nn.${name}`,
      )
    ) {
      return category;
    }
  }
  // 카테고리를 찾지 못한 경우 기본값 반환
  console.warn(`Category not found for block: ${name}, defaulting to Basic`);
  return "Basic";
}

// API 응답을 프론트엔드 블록으로 변환하는 함수
export function convertApiToBlocks(apiData: {
  layers: BackendLayer[];
}): FrontendBlock[] {
  // 입력 데이터 로깅
  console.log("Converting API data:", apiData);

  // 제외할 레이어 필터링
  const filteredLayers = apiData.layers.filter(
    (layer) => !excludedLayers.includes(layer.name),
  );

  return filteredLayers.map((layer) => {
    // 매핑된 이름 사용
    const mappedName = blockNameMapping[layer.name] || layer.name;

    // 블록 정의 찾기
    const blockDef = findBlockDefinition(mappedName);
    if (!blockDef) {
      console.error(`Unknown block type: ${mappedName}`);
      // 에러 대신 기본 블록 반환
      return {
        category: "Basic" as BlockCategory,
        name: mappedName,
        params: [],
      };
    }

    // 파라미터 매핑
    const params = blockDef.params.map((paramDef) => {
      const paramValue = layer[paramDef.name];
      return {
        ...paramDef,
        value: paramValue !== undefined ? paramValue : paramDef.value,
      };
    });

    // 변환된 블록
    const result = {
      category: findBlockCategory(mappedName),
      name: blockDef.name,
      params: params.filter((param) => param.value !== undefined),
    };

    // 변환 결과 로깅
    console.log("Converted block:", {
      original: layer,
      converted: result,
    });

    return result;
  });
}

// 프론트엔드 블록을 API 형식으로 변환하는 함수
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
