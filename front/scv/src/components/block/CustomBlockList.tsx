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
        { name: "in_channels", type: "int", min: 1, value: undefined },
        { name: "out_channels", type: "int", min: 1, value: undefined },
        { name: "kernel_size", type: "int", min: 1, value: undefined },
      ],
    },
    {
      name: "nn.ConvTranspose2d",
      params: [
        { name: "in_channels", type: "int", min: 1, value: undefined },
        { name: "out_channels", type: "int", min: 1, value: undefined },
        { name: "kernel_size", type: "int", min: 1, value: undefined },
      ],
    },
  ],
  Pooling: [
    {
      name: "MaxPool2d",
      params: [
        { name: "kernel_size", type: "int", min: 1, value: undefined },
        { name: "stride", type: "int", min: 1, value: undefined },
      ],
    },
    {
      name: "AvgPool2d",
      params: [
        { name: "kernel_size", type: "int", min: 1, value: undefined },
        { name: "stride", type: "int", min: 1, value: undefined },
      ],
    },
  ],
  Padding: [
    {
      name: "ReflectionPad2d",
      params: [{ name: "padding", type: "int", min: 0, value: undefined }],
    },
    {
      name: "ReplicationPad2d",
      params: [{ name: "padding", type: "int", min: 0, value: undefined }],
    },
    {
      name: "ZeroPad2d",
      params: [{ name: "padding", type: "int", min: 0, value: undefined }],
    },
    {
      name: "ConstantPad2d",
      params: [
        { name: "padding", type: "int", min: 0, value: undefined },
        { name: "value", type: "float", value: undefined },
      ],
    },
  ],
  Activation: [
    { name: "ReLU", params: [] },
    {
      name: "LeakyReLU",
      params: [
        { name: "negative_slope", type: "float", min: 0.0, value: undefined },
      ],
    },
    {
      name: "ELU",
      params: [{ name: "alpha", type: "float", min: 0.0, value: undefined }],
    },
    {
      name: "PReLU",
      params: [
        // { name: "num_parameters", type: "int" },
        { name: "init", type: "float", min: 0.0, value: undefined },
      ],
    },
    { name: "Sigmoid", params: [] },
    { name: "Tanh", params: [] },
    {
      name: "Softmax",
      params: [{ name: "dim", type: "int", min: 0, max: 2, value: undefined }],
    },
    {
      name: "LogSoftmax",
      params: [{ name: "dim", type: "int", min: 0, max: 2, value: undefined }],
    },
    { name: "GELU", params: [] },
  ],
  Linear: [
    {
      name: "Linear",
      params: [
        { name: "in_features", type: "int", min: 1, value: undefined },
        { name: "out_features", type: "int", min: 1, value: undefined },
      ],
    },
  ],
};
