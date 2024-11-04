"use client";

interface ButtonProps {
  onClick?: () => void;
  icon?: string;
  children?: React.ReactNode;
  disabled?: boolean;
}

const ModalButton: React.FC<ButtonProps> = ({
  onClick,
  icon,
  children,
  disabled = false,
}) => {
  return (
    <button
      className={`inline-flex shrink-0 items-center justify-center gap-[5px] whitespace-nowrap text-16 font-medium text-white hover:font-bold ${
        disabled
          ? "cursor-not-allowed opacity-50 hover:font-medium"
          : "hover:cursor-pointer"
      }`}
      onClick={onClick}
      disabled={disabled}
    >
      {icon && <span className="material-symbols-outlined">{icon}</span>}
      <div>{children}</div>
    </button>
  );
};

export default ModalButton;
