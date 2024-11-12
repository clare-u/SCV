import { Dataset } from "../dataset";
// member get
export interface UserResponse {
  userId: number;
  userEmail: string;
  userImageUrl: string;
  userNickname: string;
  userRepo?: string;
}

// 성공 응답 타입
export interface CreateRepo {
  repoName: string;
}

// github export
export interface ExportRequest {
  dataName: Dataset; // 데이터set 이름
  modelName: string; // 모델 이름
  versionNo: number; // 버전 식별자 (Long 타입을 number로 표현)
  content: string; // 블록 JSON 데이터
}
