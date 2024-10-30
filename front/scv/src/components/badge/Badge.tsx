export interface BadgeProps {
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
    | "gray";
  children: React.ReactNode;
}

const Badge: React.FC<BadgeProps> = ({ color, children }) => {
  const colorClasses = {
    red: "text-red-800 bg-red-200",
    orange: "text-orange-800 bg-orange-200",
    amber: "text-amber-800 bg-amber-200",
    yellow: "text-yellow-800 bg-yellow-200",
    lime: "text-lime-800 bg-lime-200",
    green: "text-green-800 bg-green-200",
    emerald: "text-emerald-800 bg-emerald-200",
    teal: "text-teal-800 bg-teal-200",
    cyan: "text-cyan-800 bg-cyan-200",
    sky: "text-sky-800 bg-sky-200",
    blue: "text-blue-800 bg-blue-200",
    indigo: "text-indigo-800 bg-indigo-200",
    violet: "text-violet-800 bg-violet-200",
    purple: "text-purple-800 bg-purple-200",
    fuchsia: "text-fuchsia-800 bg-fuchsia-200",
    pink: "text-pink-800 bg-pink-200",
    rose: "text-rose-800 bg-rose-200",
    gray: "text-gray-800 bg-gray-200",
  };

  return (
    <div
      className={`inline-flex w-auto items-center justify-center whitespace-nowrap rounded-[5px] px-[5px] py-2 text-12 ${colorClasses[color]}`}
    >
      {children}
    </div>
  );
};

export default Badge;
