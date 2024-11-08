"use client";

import Image from "next/image";
import Link from "next/link";

export default function LoginButton() {
  return (
    <Link
      // href="/oauth2/authorization/github"
      href="http://localhost:8080/oauth2/authorization/github"
      prefetch={false}
    >
      <button className="flex items-center justify-center gap-3 rounded-[4px] bg-black px-[60px] py-[12px] text-[18px] font-semibold text-white">
        <Image
          src="/github-mark-white.png"
          alt="GitHub Logo"
          width={24}
          height={24}
        />
        <span>Github로 로그인</span>
      </button>
    </Link>
  );
}
