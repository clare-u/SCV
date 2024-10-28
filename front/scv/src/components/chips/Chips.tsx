"use client";

interface ChipsProps {
  color:
    | "red"
    | "orange"
    | "amber"
    | "yellow"
    | "lime"
    | "green"
    | "emerald"
    | "teal"
    | "cyan"
    | "sky"
    | "blue"
    | "indigo"
    | "violet"
    | "purple"
    | "fuchsia"
    | "pink"
    | "rose"
    | "gray"
    | "black";
  design: "fill" | "outline";
  onClick?: () => void;
  children?: React.ReactNode;
}

const colorClasses: Record<
  ChipsProps["color"],
  { fill: string; outline: string }
> = {
  red: {
    fill: "text-white bg-red-600 border border-red-600",
    outline: "border border-red-600 text-red-600",
  },
  orange: {
    fill: "text-white bg-orange-600 border border-orange-600",
    outline: "border border-orange-600 text-orange-600",
  },
  amber: {
    fill: "text-white bg-amber-500 border border-amber-500",
    outline: "border border-amber-500 text-amber-500",
  },
  yellow: {
    fill: "text-white bg-yellow-600 border border-yellow-600",
    outline: "border border-yellow-600 text-yellow-600",
  },
  lime: {
    fill: "text-white bg-lime-600 border border-lime-600",
    outline: "border border-lime-600 text-lime-600",
  },
  green: {
    fill: "text-white bg-green-600 border border-green-600",
    outline: "border border-green-600 text-green-600",
  },
  emerald: {
    fill: "text-white bg-emerald-600 border border-emerald-600",
    outline: "border border-emerald-600 text-emerald-600",
  },
  teal: {
    fill: "text-white bg-teal-500 border border-teal-500",
    outline: "border border-teal-500 text-teal-500",
  },
  cyan: {
    fill: "text-white bg-cyan-600 border border-cyan-600",
    outline: "border border-cyan-600 text-cyan-600",
  },
  sky: {
    fill: "text-white bg-sky-600 border border-sky-600",
    outline: "border border-sky-600 text-sky-600",
  },
  blue: {
    fill: "text-white bg-blue-600 border border-blue-600",
    outline: "border border-blue-600 text-blue-600",
  },
  indigo: {
    fill: "text-white bg-indigo-800 border border-indigo-800",
    outline: "border border-indigo-800 text-indigo-800",
  },
  violet: {
    fill: "text-white bg-violet-600 border border-violet-600",
    outline: "border border-violet-600 text-violet-600",
  },
  purple: {
    fill: "text-white bg-purple-600 border border-purple-600",
    outline: "border border-purple-600 text-purple-600",
  },
  fuchsia: {
    fill: "text-white bg-fuchsia-600 border border-fuchsia-600",
    outline: "border border-fuchsia-600 text-fuchsia-600",
  },
  pink: {
    fill: "text-white bg-pink-600 border border-pink-600",
    outline: "border border-pink-600 text-pink-600",
  },
  rose: {
    fill: "text-white bg-rose-600 border border-rose-600",
    outline: "border border-rose-600 text-rose-600",
  },
  gray: {
    fill: "text-white bg-gray-600 border border-gray-600",
    outline: "border border-gray-600 text-gray-600",
  },
  black: {
    fill: "text-white bg-black",
    outline: "border border-black text-black",
  },
};

const Chips: React.FC<ChipsProps> = ({ color, design, onClick, children }) => {
  const colorClass = colorClasses[color][design];

  const className = `flex justify-center items-center px-10 py-[3px] rounded-[30px] whitespace-nowrap text-12 ${colorClass}`;

  return (
    <div className={className} onClick={onClick}>
      {children}
    </div>
  );
};

export default Chips;
