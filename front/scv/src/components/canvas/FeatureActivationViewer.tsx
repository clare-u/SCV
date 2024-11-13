import React from "react";
import CanvasComponent from "@/components/canvas/CanvasComponent";

interface FeatureActivationItem {
  origin: string;
  visualize: string;
}

interface FeatureActivationProps {
  featureActivation: string | FeatureActivationItem[];
}

const FeatureActivationViewer: React.FC<FeatureActivationProps> = ({
  featureActivation,
}) => {
  // Parse the data if it's a string
  const parsedData = React.useMemo(() => {
    try {
      if (typeof featureActivation === "string") {
        return JSON.parse(featureActivation);
      }
      return featureActivation;
    } catch (error) {
      console.error("Failed to parse feature activation data:", error);
      return [];
    }
  }, [featureActivation]);

  if (!Array.isArray(parsedData) || parsedData.length === 0) {
    return null;
  }

  return (
    <div className="flex flex-col gap-8 p-20">
      {parsedData.map((item, index) => (
        <div
          key={index}
          className="flex flex-col gap-4 rounded-lg border border-gray-200 p-6"
        >
          <div className="grid grid-cols-2 gap-8">
            <div className="flex flex-col items-center">
              <h3 className="mb-4 text-lg font-semibold text-gray-700">
                Original Image
              </h3>
              <div className="rounded border border-gray-200 p-4">
                <div className="">
                  <CanvasComponent data={item.origin} />
                </div>
              </div>
            </div>

            <div className="flex flex-col items-center">
              <h3 className="mb-4 text-lg font-semibold text-gray-700">
                Feature Activation
              </h3>
              <div className="rounded border border-gray-200 p-4">
                <div className="">
                  <CanvasComponent data={item.visualize} />
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default FeatureActivationViewer;
