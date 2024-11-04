"use client";

interface ButtonProps {
  onClick?: () => void;
  icon?: string;
  children?: React.ReactNode;
}

const ModalButton: React.FC<ButtonProps> = ({ onClick, icon, children }) => {
  return (
    <button
      className="inline-flex shrink-0 items-center justify-center gap-[5px] whitespace-nowrap text-16 font-medium text-white hover:font-bold"
      onClick={onClick}
    >
      {icon && <span className="material-symbols-outlined">{icon}</span>}
      <div>{children}</div>
    </button>
  );
};

export default ModalButton;
