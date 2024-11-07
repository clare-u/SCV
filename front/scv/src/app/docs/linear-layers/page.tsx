// app/docs/convolution-layers/page.tsx

import fs from "fs";
import path from "path";
import Link from "next/link";

export default function LinearLayersPage() {
  const directoryPath = path.join(process.cwd(), "content/linear-layers");
  const filenames = fs.readdirSync(directoryPath);

  // .md 파일만 필터링하고, 확장자를 제거하여 slug 리스트 생성
  const slugs = filenames
    .filter((filename) => filename.endsWith(".md"))
    .map((filename) => filename.replace(/\.md$/, ""));

  return (
    <div className="p-20">
      <header className="w-full text-[32px] font-bold text-indigo-900">
        Linear Layers
      </header>
      <ul className="ml-20 flex flex-col gap-10 p-10">
        {slugs.map((slug) => (
          <li key={slug} className="text-20 hover:underline">
            <Link href={`/docs/linear-layers/${slug}`}>
              {slug.replace(/-/g, " ")} {/* 슬러그를 사람이 읽기 쉽게 표시 */}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
