import { MemberResponse } from "@/types";
import { handleApiRequest } from "../client";

// member
export const fetchMember = async () => {
  return handleApiRequest<MemberResponse, "get">("/member", "get");
};

export const logOut = async () => {
  return handleApiRequest<void, "get">("/logout", "get");
};
