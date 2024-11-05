import Button from "@/components/button/Button";
import NewModal from "@/components/modal/NewModal";

type LayoutProps = {
  children: React.ReactNode;
};

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="flex w-[1100px] flex-col items-center gap-10 px-10 py-20">
      <header className="flex w-full justify-between py-10 text-[32px] font-bold text-indigo-900">
        내 모델
        <NewModal />
      </header>
      <main className="w-full">{children}</main>
    </div>
  );
};

export default Layout;
