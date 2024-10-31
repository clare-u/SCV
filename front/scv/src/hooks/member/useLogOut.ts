import { useMutation } from "@tanstack/react-query";
import { logOut } from "@/libs";
import { useRouter } from "next/navigation";
import { useMemberStore } from "@/store/memberStore";
import { useFetchMember } from "./useFetchMember";

export const useLogOut = () => {
  const router = useRouter();
  const clearMember = useMemberStore((state) => state.clearMember); // zustand에서 clearMember 가져오기
  // useFetchMember 훅에서 refetch 함수 가져오기
  const { refetch } = useFetchMember();

  return useMutation({
    mutationFn: () => logOut(),
    onSuccess: () => {
      // 로그아웃 성공 후 처리
      clearMember();
      // useFetchMember의 refetch 호출
      refetch();
      router.push("/");
    },
    onError: (error: Error) => {
      // 에러 발생 시 처리
      console.error("Error during logout:", error.message);
    },
  });
};
