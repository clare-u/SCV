export type BlockCategory =
  | "Basic"
  | "Convolution"
  | "Pooling"
  | "Padding"
  | "Activation"
  | "Linear";

export interface BlockParam {
  name: string;
  type: "int" | "float";
  min?: number;
  max?: number;
  value: number | undefined;
}

export interface BlockDefinition {
  name: string;
  params: BlockParam[];
}
