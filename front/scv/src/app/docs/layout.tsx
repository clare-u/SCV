"use client";

import Sidebar from "@/components/sidebar/Sidebar";
import Link from "next/link";
import { usePathname } from "next/navigation";

type LayoutProps = {
  children: React.ReactNode;
};

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const pathname = usePathname();
  const pathSegments = pathname.split("/").filter(Boolean); // 빈 문자열 필터링

  return (
    <div className="flex w-full">
      <Sidebar />
      <main className="ml-[300px] flex w-full flex-col">
        {/* 동적 링크 생성 부분 */}
        <div className="sticky top-[80px] z-50 flex items-center border-b border-gray-500 bg-white p-20">
          {pathSegments.map((segment, index) => {
            // 누적된 경로를 만들기 위해 각 세그먼트를 결합
            const path = "/" + pathSegments.slice(0, index + 1).join("/");

            // 각 세그먼트를 첫 글자만 대문자로 변경
            const formattedSegment =
              segment.charAt(0).toUpperCase() +
              segment.slice(1).replace("-", " ");

            return (
              <span key={path} className="flex items-center">
                <Link href={path} className="text-blue-500 hover:underline">
                  {formattedSegment}
                </Link>
                {index < pathSegments.length - 1 && (
                  <span className="mx-2"> &gt; </span>
                )}
              </span>
            );
          })}
        </div>
        {children}
      </main>
    </div>
  );
};

export default Layout;
