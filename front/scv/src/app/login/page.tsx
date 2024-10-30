import LoginButton from "@/components/button/LoginButton";
import Image from "next/image";

export default function Login() {
  return (
    <div className="flex h-[85vh] w-full flex-col items-center justify-center gap-[60px]">
      <Image src="/logo-dark.png" alt="scv logo" width={595} height={217} />
      <LoginButton />
    </div>
  );
}
