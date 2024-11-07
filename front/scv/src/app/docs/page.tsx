// app/docs/page.tsx

import fs from "fs";
import path from "path";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

export default async function DocsPage() {
  // `content/docs.md` 파일 경로 설정
  const filePath = path.join(process.cwd(), "content", "docs.md");

  // 파일 내용 읽기 (비동기)
  const content = fs.readFileSync(filePath, "utf8");

  return (
    <div className="prose max-w-none p-20">
      <ReactMarkdown remarkPlugins={[remarkGfm]}>{content}</ReactMarkdown>
    </div>
  );
}
