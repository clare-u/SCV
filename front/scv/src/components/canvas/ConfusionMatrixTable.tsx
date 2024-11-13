"use client";
import React from "react";

interface ConfusionMatrixTableProps {
  data: string;
}

const ConfusionMatrixTable = ({ data }: ConfusionMatrixTableProps) => {
  let matrix: number[][] = [];

  try {
    const parsedOnce = JSON.parse(data);
    const parsedTwice = JSON.parse(parsedOnce);
    matrix = parsedTwice;

    if (!Array.isArray(matrix) || !matrix.every((row) => Array.isArray(row))) {
      throw new Error("2차원 배열 형식이 아닙니다");
    }
  } catch (error) {
    console.error("데이터 파싱 오류:", error);
    return (
      <div className="p-4 text-red-500">데이터 형식이 올바르지 않습니다</div>
    );
  }

  return (
    <div className="p-20">
      <table className="border-collapse border border-gray-300">
        <thead>
          <tr>
            <th className="h-40 w-40 border border-gray-300 bg-gray-200 align-middle">
              T \ P
            </th>
            {matrix[0].map((_, index) => (
              <th
                key={index}
                className="h-40 w-40 border border-gray-300 bg-gray-200 align-middle"
              >
                {index}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {matrix.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <th className="h-40 w-40 border border-gray-300 bg-gray-200 align-middle">
                {rowIndex}
              </th>
              {row.map((value, colIndex) => (
                <td
                  key={colIndex}
                  className={`h-40 w-40 border text-center align-middle ${
                    rowIndex === colIndex
                      ? "border-blue-300 bg-blue-100 font-bold text-blue-800"
                      : "border-gray-300 hover:bg-gray-50"
                  }`}
                >
                  {value}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ConfusionMatrixTable;
