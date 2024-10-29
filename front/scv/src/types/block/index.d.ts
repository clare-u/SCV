export type BlockCategory =
  | "Convolution"
  | "Pooling"
  | "Padding"
  | "Activation"
  | "Linear";

export interface BlockParam {
  name: string;
  type: "int" | "float";
}

export interface BlockDefinition {
  name: string;
  params: BlockParam[];
}
