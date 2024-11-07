import { MemberResponse } from "@/types";
import { handleApiRequest } from "../client";

// member
export const fetchMember = async () => {
  return handleApiRequest<MemberResponse, "get">("/users", "get");
};

export const logOut = async () => {
  return handleApiRequest<void, "post">("/logout", "post");
};
