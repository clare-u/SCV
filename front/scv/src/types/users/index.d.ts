// member get
export interface UserResponse {
  userId: number;
  userEmail: string;
  userImageUrl: string;
  userNickname: string;
  userRepo?: string;
}

// 성공 응답 타입
export interface CreateRepoSuccessResponse {
  repoName: string;
}

// 실패 응답 타입
export interface CreateRepoErrorResponse {
  httpStatus: number;
  code: string;
  message: string;
}

// 요청 body 타입
export interface CreateRepoRequest {
  repoName: string;
}

// 성공과 실패 응답을 모두 포함하는 타입
export type CreateRepoResponse =
  | CreateRepoSuccessResponse
  | CreateRepoErrorResponse;
