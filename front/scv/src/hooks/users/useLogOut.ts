import { useMutation } from "@tanstack/react-query";
import { logOut } from "@/libs";
import { useRouter } from "next/navigation";
import { useUserStore } from "@/store/userStore";
import { useFetchUser } from "./useFetchUser";

export const useLogOut = () => {
  const router = useRouter();
  const clearUser = useUserStore((state) => state.clearUser); // zustand에서 clearMember 가져오기
  // useFetchMember 훅에서 refetch 함수 가져오기
  const { refetch } = useFetchUser();

  return useMutation({
    mutationFn: () => logOut(),
    onSuccess: () => {
      // 로그아웃 성공 후 처리
      clearUser();
      // useFetchUser의 refetch 호출
      refetch();
      router.push("/");
    },
    onError: (error: Error) => {
      // 에러 발생 시 처리
      console.error("Error during logout:", error.message);
    },
  });
};
