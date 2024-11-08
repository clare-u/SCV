import { MemberResponse } from "@/types";
import { handleApiRequest } from "../client";

// member
export const fetchMember = async () => {
  return handleApiRequest<MemberResponse, "get">("/api/v1/users", "get");
};

export const logOut = async () => {
  return handleApiRequest<void, "post">("/api/v1/logout", "post");
};
