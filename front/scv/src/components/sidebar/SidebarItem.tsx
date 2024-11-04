"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

interface NavigationItemProps {
  href: string;
  children: React.ReactNode;
}

const SidebarItem: React.FC<NavigationItemProps> = ({ href, children }) => {
  const pathname = usePathname();
  // 정확히 일치하거나 href가 '/'가 아닐 때만 활성화
  const isActive = href === "/" ? pathname === href : pathname.startsWith(href);

  return (
    <Link
      href={href}
      className={`flex w-full whitespace-nowrap px-20 py-20 text-16 ${isActive ? "font-extrabold" : ""} hover:bg-stone-300`}
    >
      {children}
    </Link>
  );
};

export default SidebarItem;
