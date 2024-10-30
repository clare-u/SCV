import type { Metadata } from "next";
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
          {children}
        </QueryProvider>
      </body>
    </html>
  );
}
