import { UserResponse, CreateRepoRequest, CreateRepoResponse } from "@/types";
import { handleApiRequest } from "../client";

// member
export const fetchUser = async () => {
  return handleApiRequest<UserResponse, "get">("/api/v1/users", "get");
};

export const logOut = async () => {
  return handleApiRequest<void, "post">("/api/v1/logout", "post");
};

export const createRepo = async (data: CreateRepoRequest) => {
  return handleApiRequest<CreateRepoResponse, "post", CreateRepoRequest>(
    "/api/v1/users/repo",
    "post",
    data,
  );
};
