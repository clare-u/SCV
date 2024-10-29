"use client";
import { usePathname } from "next/navigation";
import Navigation from "./Navigation";

export const NavigationWrapper = () => {
  const pathname = usePathname();
  const hideNavigation = pathname?.startsWith("/workspace/edit");

  if (hideNavigation) {
    return null;
  }

  return <Navigation />;
};
