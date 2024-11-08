import axios, { AxiosError } from "axios";
import {
  ApiErrorResponse,
  ApiRequestConfig,
  ConditionalData,
  HttpMethod,
} from "@/types";

const api = axios.create({
  // baseURL: "https://k11a107.p.ssafy.io",
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

export const handleApiRequest = async <T, M extends HttpMethod, D = undefined>(
  url: string,
  method: M,
  data?: ConditionalData<M, D>,
  config?: ApiRequestConfig,
): Promise<T> => {
  try {
    const response = await api.request<T>({
      url,
      method,
      data,
      ...config,
    });
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      const apiError: ApiErrorResponse = error.response.data;
      const { message, httpStatus, code } = apiError;

      // 콘솔에 개선된 오류 메시지 출력
      console.error(
        `%c🚨 API Request Error 🚨\n%cEndpoint: %s\n%cHTTP Status: %s\n%cError Code: %s\n%cMessage: %s`,
        "color: red; font-weight: bold; font-size: 16px; background-color: yellow; padding: 2px;",
        "color: #333; font-weight: bold; background: #f1f1f1; padding: 2px; border-radius: 3px;",
        url,
        "color: #ff6347; font-weight: bold;",
        httpStatus,
        "color: #ff6347; font-weight: bold;",
        code,
        "color: #ff6347; font-weight: bold;",
        message,
      );

      throw apiError; // 전체 에러 객체 throw
    }

    console.error("🚨 Unexpected error making API request 🚨\n", error);
    throw error;
  }
};

export default api;
