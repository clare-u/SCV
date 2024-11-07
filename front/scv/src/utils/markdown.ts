// /utils/markdown.ts
import fs from "fs";
import path from "path";
import matter from "gray-matter";
import { remark } from "remark";
import html from "remark-html";

const contentDirectory = path.join(process.cwd(), "content");

export async function getMarkdownContent(slug: string) {
  const filePath = path.join(contentDirectory, `${slug}.md`);
  const fileContent = fs.readFileSync(filePath, "utf8");

  // gray-matter를 사용해 메타데이터 파싱
  const { data, content } = matter(fileContent);

  // remark를 사용해 마크다운을 HTML로 변환
  const processedContent = await remark().use(html).process(content);
  const contentHtml = processedContent.toString();

  return {
    slug,
    meta: data, // YAML Front Matter 메타데이터
    contentHtml,
  };
}
