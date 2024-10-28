"use client";

interface ButtonProps {
  size: "l" | "m" | "s";
  design: "fill" | "outline";
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
    | "rose";
  disabled?: boolean;
  onClick?: () => void;
  icon?: string;
  children?: React.ReactNode;
}

const colorStyles: Record<
  ButtonProps["color"],
  { fill: string; outline: string }
> = {
  red: {
    fill: "text-white bg-red-800 hover:bg-red-900 disabled:bg-gray-400",
    outline:
      "text-red-800 bg-white border border-red-800 hover:text-red-900 hover:border-red-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  orange: {
    fill: "text-white bg-orange-800 hover:bg-orange-900 disabled:bg-gray-400",
    outline:
      "text-orange-800 bg-white border border-orange-800 hover:text-orange-900 hover:border-orange-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  amber: {
    fill: "text-white bg-amber-800 hover:bg-amber-900 disabled:bg-gray-400",
    outline:
      "text-amber-800 bg-white border border-amber-800 hover:text-amber-900 hover:border-amber-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  yellow: {
    fill: "text-white bg-yellow-800 hover:bg-yellow-900 disabled:bg-gray-400",
    outline:
      "text-yellow-800 bg-white border border-yellow-800 hover:text-yellow-900 hover:border-yellow-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  lime: {
    fill: "text-white bg-lime-800 hover:bg-lime-900 disabled:bg-gray-400",
    outline:
      "text-lime-800 bg-white border border-lime-800 hover:text-lime-900 hover:border-lime-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  green: {
    fill: "text-white bg-green-800 hover:bg-green-900 disabled:bg-gray-400",
    outline:
      "text-green-800 bg-white border border-green-800 hover:text-green-900 hover:border-green-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  emerald: {
    fill: "text-white bg-emerald-800 hover:bg-emerald-900 disabled:bg-gray-400",
    outline:
      "text-emerald-800 bg-white border border-emerald-800 hover:text-emerald-900 hover:border-emerald-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  teal: {
    fill: "text-white bg-teal-800 hover:bg-teal-900 disabled:bg-gray-400",
    outline:
      "text-teal-800 bg-white border border-teal-800 hover:text-teal-900 hover:border-teal-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  cyan: {
    fill: "text-white bg-cyan-800 hover:bg-cyan-900 disabled:bg-gray-400",
    outline:
      "text-cyan-800 bg-white border border-cyan-800 hover:text-cyan-900 hover:border-cyan-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  sky: {
    fill: "text-white bg-sky-800 hover:bg-sky-900 disabled:bg-gray-400",
    outline:
      "text-sky-800 bg-white border border-sky-800 hover:text-sky-900 hover:border-sky-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  blue: {
    fill: "text-white bg-blue-800 hover:bg-blue-900 disabled:bg-gray-400",
    outline:
      "text-blue-800 bg-white border border-blue-800 hover:text-blue-900 hover:border-blue-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  indigo: {
    fill: "text-white bg-indigo-800 hover:bg-indigo-900 disabled:bg-gray-400",
    outline:
      "text-indigo-800 bg-white border border-indigo-800 hover:text-indigo-900 hover:border-indigo-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  violet: {
    fill: "text-white bg-violet-800 hover:bg-violet-900 disabled:bg-gray-400",
    outline:
      "text-violet-800 bg-white border border-violet-800 hover:text-violet-900 hover:border-violet-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  purple: {
    fill: "text-white bg-purple-800 hover:bg-purple-900 disabled:bg-gray-400",
    outline:
      "text-purple-800 bg-white border border-purple-800 hover:text-purple-900 hover:border-purple-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  fuchsia: {
    fill: "text-white bg-fuchsia-800 hover:bg-fuchsia-900 disabled:bg-gray-400",
    outline:
      "text-fuchsia-800 bg-white border border-fuchsia-800 hover:text-fuchsia-900 hover:border-fuchsia-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  pink: {
    fill: "text-white bg-pink-800 hover:bg-pink-900 disabled:bg-gray-400",
    outline:
      "text-pink-800 bg-white border border-pink-800 hover:text-pink-900 hover:border-pink-900 disabled:text-gray-400 disabled:border-gray-400",
  },
  rose: {
    fill: "text-white bg-rose-800 hover:bg-rose-900 disabled:bg-gray-400",
    outline:
      "text-rose-800 bg-white border border-rose-800 hover:text-rose-900 hover:border-rose-900 disabled:text-gray-400 disabled:border-gray-400",
  },
};

const Button: React.FC<ButtonProps> = ({
  size,
  design,
  color,
  disabled = false,
  onClick,
  icon,
  children,
}) => {
  const sizeClasses = {
    l: "flex whitespace-nowrap justify-center items-center h-56 p-16 text-17 gap-8 rounded-12",
    m: "flex whitespace-nowrap justify-center items-center h-48 p-16 text-16 gap-8 rounded-10",
    s: "flex whitespace-nowrap justify-center items-center h-40 p-16 text-15 gap-8 rounded-8",
  };

  const colorClasses = colorStyles[color][design];
  const className = `${sizeClasses[size]} ${colorClasses} ${
    disabled ? "cursor-not-allowed" : "cursor-pointer"
  }`;

  return (
    <button className={className} disabled={disabled} onClick={onClick}>
      {icon && <span className="material-symbols-outlined">{icon}</span>}
      <div className="font-semibold">{children}</div>
    </button>
  );
};

export default Button;
