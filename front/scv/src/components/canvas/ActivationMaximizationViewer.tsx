import CanvasComponent from "./CanvasComponent";

interface ActivationMaximizationViewerProps {
  activationData:
    | string
    | {
        image: number[][];
        label: string;
      }[];
}

const ActivationMaximizationViewer: React.FC<
  ActivationMaximizationViewerProps
> = ({ activationData }) => {
  try {
    // 문자열인 경우 JSON 파싱
    const parsedData =
      typeof activationData === "string"
        ? JSON.parse(activationData)
        : activationData;

    // 배열이 아닌 경우 배열로 변환
    const dataArray = Array.isArray(parsedData) ? parsedData : [parsedData];

    return (
      <div className="w-full p-20">
        <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-5">
          {dataArray.map((data, index) => (
            <div key={index} className="flex flex-col items-center">
              <div className="inline-block border-2 border-gray-500 bg-white p-4">
                <CanvasComponent
                  data={
                    typeof data.image === "string"
                      ? data.image
                      : JSON.stringify(data.image)
                  }
                />
              </div>
              <span className="mt-2 text-sm font-medium text-gray-700">
                Label: {data.label}
              </span>
            </div>
          ))}
        </div>
      </div>
    );
  } catch (error) {
    console.error("Error parsing activation data:", error);
    return (
      <div className="w-full p-4 text-red-600">
        Error loading activation maximization data
      </div>
    );
  }
};

export default ActivationMaximizationViewer;
