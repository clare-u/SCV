import { useQuery } from "@tanstack/react-query";
import { MemberResponse } from "@/types";
import { fetchMember } from "@/libs";

export const useFetchMember = () => {
  return useQuery<MemberResponse, Error>({
    queryKey: ["member"],
    queryFn: async () => {
      const data = await fetchMember();
      console.log("Member Data:", data);
      return data;
    },
  });
};
