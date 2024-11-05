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
        { name: "in_channels", type: "int", min: 1 },
        { name: "out_channels", type: "int", min: 1 },
        { name: "kernel_size", type: "int", min: 1 },
      ],
    },
    {
      name: "nn.ConvTranspose2d",
      params: [
        { name: "in_channels", type: "int", min: 1 },
        { name: "out_channels", type: "int", min: 1 },
        { name: "kernel_size", type: "int", min: 1 },
      ],
    },
  ],
  Pooling: [
    {
      name: "MaxPool2d",
      params: [
        { name: "kernel_size", type: "int", min: 1 },
        { name: "stride", type: "int", min: 1 },
      ],
    },
    {
      name: "AvgPool2d",
      params: [
        { name: "kernel_size", type: "int", min: 1 },
        { name: "stride", type: "int", min: 1 },
      ],
    },
  ],
  Padding: [
    {
      name: "ReflectionPad2d",
      params: [{ name: "padding", type: "int", min: 0 }],
    },
    {
      name: "ReplicationPad2d",
      params: [{ name: "padding", type: "int", min: 0 }],
    },
    { name: "ZeroPad2d", params: [{ name: "padding", type: "int", min: 0 }] },
    {
      name: "ConstantPad2d",
      params: [
        { name: "padding", type: "int", min: 0 },
        { name: "value", type: "float" },
      ],
    },
  ],
  Activation: [
    { name: "ReLU", params: [] },
    {
      name: "LeakyReLU",
      params: [{ name: "negative_slope", type: "float", min: 0.0 }],
    },
    { name: "ELU", params: [{ name: "alpha", type: "float", min: 0.0 }] },
    {
      name: "PReLU",
      params: [
        // { name: "num_parameters", type: "int" },
        { name: "init", type: "float", min: 0.0 },
      ],
    },
    { name: "Sigmoid", params: [] },
    { name: "Tanh", params: [] },
    { name: "Softmax", params: [{ name: "dim", type: "int", min: 0, max: 2 }] },
    {
      name: "LogSoftmax",
      params: [{ name: "dim", type: "int", min: 0, max: 2 }],
    },
    { name: "GELU", params: [] },
  ],
  Linear: [
    {
      name: "Linear",
      params: [
        { name: "in_features", type: "int", min: 1 },
        { name: "out_features", type: "int", min: 1 },
      ],
    },
  ],
};
