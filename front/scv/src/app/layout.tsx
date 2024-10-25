import type { Metadata } from "next";
import Navigation from "@/components/navigation/Navigation";
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
          {/* // TODO: /workspace/edit 부분에서 네비게이션 렌더링 하지 않는 로직 */}
          <Navigation /> {children}
        </QueryProvider>
      </body>
    </html>
  );
}
