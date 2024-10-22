import Link from "next/link";

export default function Home() {
  return (
    <div>
      <Link href="/login" passHref>
        <button>go to login</button>
      </Link>
    </div>
  );
}
