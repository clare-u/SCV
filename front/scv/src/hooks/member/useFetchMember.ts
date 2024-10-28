import { useQuery } from "@tanstack/react-query";
import { MemberResponse } from "@/types";
import { fetchMember } from "@/libs";

export const useFetchMember = () => {
  return useQuery<MemberResponse, Error>({
    queryKey: ["member"],
    queryFn: () => fetchMember(),
  });
};
