import type { Metadata } from "next";
import { Toaster } from "sonner";
import { NavigationWrapper } from "@/components/navigation/NavigationWrapper";
import QueryProvider from "@/components/QueryProvider";

import "./globals.css";

export const metadata: Metadata = {
  title: "SCV",
  description: "SSAFY Computer Vision",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <head>
        <link
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined"
          rel="stylesheet"
        />
      </head>
      <body>
        <QueryProvider>
          <NavigationWrapper />
          <div className="flex h-full flex-col items-center justify-center">
            {children}
          </div>
          <Toaster richColors position="top-right" />
        </QueryProvider>
      </body>
    </html>
  );
}
