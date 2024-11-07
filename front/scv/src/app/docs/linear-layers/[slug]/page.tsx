import fs from "fs";
import path from "path";
import matter from "gray-matter";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

interface PageProps {
  params: { slug: string };
}

export async function generateStaticParams() {
  const directoryPath = path.join(process.cwd(), "content/linear-layers");
  const filenames = fs.readdirSync(directoryPath);

  return filenames
    .filter((filename) => filename.endsWith(".md"))
    .map((filename) => ({
      slug: filename.replace(/\.md$/, ""),
    }));
}

export default function Post({ params }: PageProps) {
  const { slug } = params;
  const filePath = path.join(
    process.cwd(),
    "content/convolution-layers",
    `${slug}.md`,
  );
  const fileContent = fs.readFileSync(filePath, "utf8");

  const { content } = matter(fileContent);

  return (
    <article className="prose max-w-none p-20">
      <ReactMarkdown remarkPlugins={[remarkGfm]}>{content}</ReactMarkdown>
    </article>
  );
}
