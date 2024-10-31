import { BlockCategory, BlockDefinition } from "@/types";

export const CustomBlockList: Record<BlockCategory, BlockDefinition[]> = {
  Basic: [
    { name: "start", params: [] },
    { name: "end", params: [] },
  ],
  Convolution: [
    {
      name: "nn.Conv2d",
      params: [
        { name: "in_channels", type: "int", min: 1, max: 512 },
        { name: "out_channels", type: "int", min: 1, max: 512 },
        { name: "kernel_size", type: "int", min: 1, max: 7 },
      ],
    },
    {
      name: "nn.ConvTranspose2d",
      params: [
        { name: "in_channels", type: "int" },
        { name: "out_channels", type: "int" },
        { name: "kernel_size", type: "int" },
      ],
    },
  ],
  Pooling: [
    {
      name: "MaxPool2d",
      params: [
        { name: "kernel_size", type: "int" },
        { name: "stride", type: "int" },
      ],
    },
    {
      name: "AvgPool2d",
      params: [
        { name: "kernel_size", type: "int" },
        { name: "stride", type: "int" },
      ],
    },
  ],
  Padding: [
    { name: "ReflectionPad2d", params: [{ name: "padding", type: "int" }] },
    { name: "ReplicationPad2d", params: [{ name: "padding", type: "int" }] },
    { name: "ZeroPad2d", params: [{ name: "padding", type: "int" }] },
    {
      name: "ConstantPad2d",
      params: [
        { name: "padding", type: "int" },
        { name: "value", type: "float" },
      ],
    },
  ],
  Activation: [
    { name: "ReLU", params: [] },
    { name: "LeakyReLU", params: [{ name: "negative_slope", type: "float" }] },
    { name: "ELU", params: [{ name: "alpha", type: "float" }] },
    {
      name: "PReLU",
      params: [
        { name: "num_parameters", type: "int" },
        { name: "init", type: "float" },
      ],
    },
    { name: "Sigmoid", params: [] },
    { name: "Tanh", params: [] },
    { name: "Softmax", params: [{ name: "dim", type: "int" }] },
    { name: "LogSoftmax", params: [{ name: "dim", type: "int" }] },
    { name: "GELU", params: [] },
  ],
  Linear: [
    {
      name: "Linear",
      params: [
        { name: "in_features", type: "int" },
        { name: "out_features", type: "int" },
      ],
    },
  ],
};
