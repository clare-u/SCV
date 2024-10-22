import type { Metadata } from "next";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import "./globals.css";

export const metadata: Metadata = {
  title: "SCV",
  description: "SSAFY Computer Vision",
};

const queryClient = new QueryClient();

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
        <QueryClientProvider client={queryClient}>
          {children}
        </QueryClientProvider>
      </body>
    </html>
  );
}
