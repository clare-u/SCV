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
      // canvas의 가로, 세로 크기를 두 배로 설정
      canvas.width = width * 2;
      canvas.height = height * 2;

      const ctx = canvas.getContext("2d");
      if (ctx) {
        const imageData = ctx.createImageData(width * 2, height * 2); // 크기를 두 배로 확장

        // 원본 이미지를 두 배 크기로 확대하여 처리
        for (let y = 0; y < height; y++) {
          for (let x = 0; x < width; x++) {
            const value = parsedData[y][x];
            const color = Math.floor(((value + 1) / 2) * 255); // -1 ~ 1 범위를 0 ~ 255로 매핑
            const invertedColor = 255 - color; // 흑백 반전

            // 원본 크기에서 각 픽셀을 두 배 크기로 확대하여 RGBA 채널에 대응
            for (let dy = 0; dy < 2; dy++) {
              for (let dx = 0; dx < 2; dx++) {
                const index = ((y * 2 + dy) * width * 2 + (x * 2 + dx)) * 4; // 확대된 이미지에 맞는 인덱스 계산
                imageData.data[index] = invertedColor; // Red
                imageData.data[index + 1] = invertedColor; // Green
                imageData.data[index + 2] = invertedColor; // Blue
                imageData.data[index + 3] = 255; // Alpha (불투명)
              }
            }
          }
        }

        ctx.putImageData(imageData, 0, 0); // 확대된 이미지를 캔버스에 그리기
      }
    }
  }, [data]);

  return <canvas ref={canvasRef}></canvas>;
};

export default CanvasComponent;
