import { useQuery } from "@tanstack/react-query";
import { UserResponse } from "@/types";
import { fetchUser } from "@/libs";

export const useFetchUser = () => {
  return useQuery<UserResponse, Error>({
    queryKey: ["member"],
    queryFn: async () => {
      const data = await fetchUser();
      console.log("Member Data:", data);
      return data;
    },
  });
};
