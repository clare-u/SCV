"use client";
import NewModal from "@/components/modal/NewModal";
import { usePathname } from "next/navigation";

type LayoutProps = {
  children: React.ReactNode;
};

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const pathname = usePathname();

  // 동적 라우트 매칭을 위한 정규식 패턴
  const isModelVersionRoute = /^\/workspace\/\d+\/\d+/.test(pathname);

  // modelId/versionId 경로에서는 레이아웃을 렌더링하지 않음
  if (isModelVersionRoute) {
    return <>{children}</>;
  }

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
