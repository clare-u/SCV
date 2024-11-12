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
  // Convolution
  Conv2d: "nn.Conv2d",
  ConvTranspose2d: "nn.ConvTranspose2d",

  // Pooling
  MaxPool2d: "MaxPool2d",
  AvgPool2d: "AvgPool2d",

  // Padding
  ReflectionPad2d: "ReflectionPad2d",
  ReplicationPad2d: "ReplicationPad2d",
  ZeroPad2d: "ZeroPad2d",
  ConstantPad2d: "ConstantPad2d",

  // Activation
  ReLU: "ReLU",
  LeakyReLU: "LeakyReLU",
  ELU: "ELU",
  PReLU: "PReLU",
  Sigmoid: "Sigmoid",
  Tanh: "Tanh",
  Softmax: "Softmax",
  LogSoftmax: "LogSoftmax",
  GELU: "GELU",

  // Linear
  Linear: "Linear",
} as const;

// 백엔드에서 허용하는 타입 목록 (타입 체크용)
const allowedBackendTypes = [
  "AvgPool2d",
  "ConstantPad2d",
  "Conv2d",
  "ConvTranspose2d",
  "ELU",
  "Flatten",
  "GELU",
  "LeakyReLU",
  "Linear",
  "LogSoftmax",
  "MaxPool2d",
  "PReLU",
  "ReLU",
  "ReflectionPad2d",
  "ReplicationPad2d",
  "Sigmoid",
  "Softmax",
  "Tanh",
  "ZeroPad2d",
] as const;

// 타입 안전성을 위한 타입 가드 함수
function isValidLayerType(
  type: string,
): type is (typeof allowedBackendTypes)[number] {
  return allowedBackendTypes.includes(type as any);
}

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

// findBlockCategory 함수 수정
export function findBlockCategory(name: string): BlockCategory {
  // nn. 접두사 제거
  const cleanName = name.replace("nn.", "");

  // 각 카테고리별 매칭 규칙
  if (cleanName.includes("Conv")) return "Convolution";
  if (cleanName.includes("Pool")) return "Pooling";
  if (cleanName.includes("Pad")) return "Padding";
  if (cleanName.includes("Linear")) return "Linear";
  if (
    cleanName.includes("ReLU") ||
    cleanName.includes("ELU") ||
    cleanName.includes("Sigmoid") ||
    cleanName.includes("Tanh") ||
    cleanName.includes("Softmax") ||
    cleanName.includes("GELU") ||
    cleanName.includes("PReLU")
  )
    return "Activation";

  return "Basic";
}

export function convertBlocksToApiFormat(blocks: BlockDefinition[]): {
  layers: BackendLayer[];
} {
  const layers = blocks
    // start와 end 블록 제외
    .filter((block) => block.name !== "start" && block.name !== "end")
    .map((block) => {
      // nn. 접두사 제거
      const backendName = block.name.replace("nn.", "");

      // 기본 레이어 객체 생성
      const layer: BackendLayer = { name: backendName };

      // 파라미터가 있는 경우 추가
      if (block.params && block.params.length > 0) {
        block.params.forEach((param) => {
          if (param.value !== undefined) {
            layer[param.name] = param.value;
          }
        });
      }

      // 변환된 레이어 로깅
      console.log("Converting block to layer:", {
        originalName: block.name,
        convertedName: backendName,
        layer,
      });

      return layer;
    });

  return { layers };
}

// convertApiToBlocks 함수도 수정
export function convertApiToBlocks(apiData: {
  layers: BackendLayer[];
}): BlockDefinition[] {
  return apiData.layers.map((layer) => {
    const blockDef = findBlockDefinition(layer.name);
    if (!blockDef) {
      console.error(`Unknown block type: ${layer.name}`);
      return {
        name: layer.name,
        params: [],
        category: findBlockCategory(layer.name),
      };
    }

    const params = blockDef.params.map((paramDef) => ({
      ...paramDef,
      value: layer[paramDef.name],
    }));

    return {
      name: blockDef.name,
      params: params.filter((param) => param.value !== undefined),
      category: findBlockCategory(blockDef.name),
    };
  });
}
