"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import NavigationItem from "./NavigationItem";
import NavigationProfile from "./NavigationProfile";
import { useMemberStore } from "@/store/memberStore";
import { useFetchMember } from "@/hooks";

const Navigation = () => {
  const member = useMemberStore((state) => state.member); // zustand에서 현재 member 상태 가져오기
  const setMember = useMemberStore((state) => state.setMember); // zustand에서 setMember 가져오기
  const [fetchData, setFetchData] = useState(false); // API 호출 여부를 제어하기 위한 로컬 상태

  useEffect(() => {
    if (!member) {
      setFetchData(true); // member가 없으면 데이터를 받아오도록 설정
    }
  }, [member]);

  const { data: memberData, isLoading, error } = useFetchMember(); // 인자 없이 호출

  useEffect(() => {
    if (memberData && !member && fetchData) {
      setMember(memberData); // member 상태가 없을 때만 zustand에 저장
      setFetchData(false); // 데이터를 받아온 후 다시 API 호출을 막기 위해 설정
    }
  }, [memberData, member, setMember, fetchData]);

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
        {memberData ? (
          <NavigationProfile
            image={memberData.userImageUrl}
            nickname={memberData.userNickname}
            email={memberData.userEmail}
          />
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
