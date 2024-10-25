import Link from "next/link";

export default function Home() {
  return (
    <div className="flex h-screen items-center justify-center">
      <Link href="/login" passHref>
        <button>go to login</button>
      </Link>
    </div>
  );
}
