import {
  fontSize,
  fontWeight,
  lineHeight,
  radius,
  shadow,
  spacing,
} from "./src/styles";

import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
      },
      fontFamily: {
        sans: ["Pretendard"],
      },
      fontSize: {
        ...fontSize, // 사용자 정의 폰트 크기 확장
      },
      fontWeight: {
        ...fontWeight, // 사용자 정의 폰트 두께 확장
      },
      lineHeight: {
        ...lineHeight, // 사용자 정의 줄 높이 확장
      },
      borderRadius: {
        ...radius, // 사용자 정의 보더 반경 확장
      },
      boxShadow: {
        ...shadow, // 사용자 정의 그림자 확장
      },
      spacing: {
        ...spacing, // 사용자 정의 여백 확장
      },
    },
  },
  plugins: [],
};
export default config;
