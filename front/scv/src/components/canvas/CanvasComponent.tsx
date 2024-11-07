"use client";

import { useEffect, useRef } from "react";

interface CanvasComponentProps {
  data: string; // data JSON 문자열
}

const CanvasComponent: React.FC<CanvasComponentProps> = ({ data }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const parsedData: number[][] = JSON.parse(data); // data를 파싱하여 2차원 배열로 처리
    const height = parsedData.length;
    const width = parsedData[0].length;

    const canvas = canvasRef.current;
    if (canvas) {
      canvas.width = width;
      canvas.height = height;

      const ctx = canvas.getContext("2d");
      if (ctx) {
        const imageData = ctx.createImageData(width, height);

        for (let y = 0; y < height; y++) {
          for (let x = 0; x < width; x++) {
            const value = parsedData[y][x];
            const color = Math.floor(((value + 1) / 2) * 255); // -1 ~ 1 범위를 0 ~ 255로 매핑
            const invertedColor = 255 - color; // 흑백 반전

            const index = (y * width + x) * 4; // RGBA 채널
            imageData.data[index] = invertedColor; // Red
            imageData.data[index + 1] = invertedColor; // Green
            imageData.data[index + 2] = invertedColor; // Blue
            imageData.data[index + 3] = 255; // Alpha (불투명)
          }
        }

        ctx.putImageData(imageData, 0, 0);
      }
    }
  }, [data]);

  return <canvas ref={canvasRef}></canvas>;
};

export default CanvasComponent;
