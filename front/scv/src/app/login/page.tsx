"use client";
import { useEffect } from "react";
import LoginButton from "@/components/button/LoginButton";
import Image from "next/image";
import { toast } from "sonner";

export default function Login() {
  useEffect(() => {
    // 컴포넌트가 마운트된 후 약간의 지연을 두고 메시지 체크
    const timer = setTimeout(() => {
      const message = localStorage.getItem("redirect_message");
      if (message) {
        toast.error(message);
        localStorage.removeItem("redirect_message");
      }
    }, 50); // 50ms 지연

    return () => clearTimeout(timer); // cleanup
  }, []);

  return (
    <div className="flex h-[85vh] w-full flex-col items-center justify-center gap-[60px]">
      <Image src="/logo-dark.png" alt="scv logo" width={595} height={217} />
      <LoginButton />
    </div>
  );
}
