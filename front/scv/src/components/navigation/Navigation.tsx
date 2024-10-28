"use client";

import Link from "next/link";
import Image from "next/image";
import NavigationItem from "./NavigationItem";
import NavigationProfile from "./NavigationProfile";

interface MemberData {
  nickname: string;
  image: string;
  email: string;
}

const Navigation = () => {
  // TODO: 로그인 정보를 받아와서 isLogin 삭제
  // TODO: 로그인 정보를 받아와서 더미 데이터 삭제

  const isLogin = false; // 임의로 설정
  const memberData: MemberData | null = {
    // 더미 데이터
    nickname: "nickname",
    image: "/profile.png",
    email: "email@email.com",
  };

  return (
    <nav className="sticky top-0 z-50 flex h-[80px] items-center justify-between bg-indigo-800 px-40">
      {/* 로고 영역 */}
      <Link href="/">
        <Image src="/logo.png" alt="Logo" width={110} height={40} priority />
      </Link>

      {/* 메뉴 영역 */}
      <div className="flex gap-36 p-10">
        <NavigationItem href="/workspace">워크스페이스</NavigationItem>
        <NavigationItem href="/">커뮤니티</NavigationItem>
        <NavigationItem href="/docs">공식문서</NavigationItem>
      </div>

      {/* 프로필 영역 */}
      <div className="flex gap-36 p-10">
        {isLogin ? (
          memberData ? (
            <NavigationProfile
              image={memberData.image}
              nickname={memberData.nickname}
              email={memberData.email}
            />
          ) : (
            <NavigationItem href="/login" icon="login">
              login
            </NavigationItem>
          )
        ) : (
          <NavigationItem href="/login" icon="login">
            login
          </NavigationItem>
        )}
      </div>
    </nav>
  );
};

export default Navigation;
