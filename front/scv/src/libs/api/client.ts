import axios, { AxiosError } from "axios";
import {
  ApiErrorResponse,
  ApiRequestConfig,
  ConditionalData,
  HttpMethod,
} from "@/types";

const api = axios.create({
  // baseURL: "https://배포url/api/v1",
  baseURL: "http://localhost:8080/api/v1",
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
      const { message, data, statusCode } = apiError;

      // 콘솔에 개선된 오류 메시지 출력
      console.error(
        `%c🚨 API Request Error 🚨\nEndpoint: %c${url}\nStatus Code: %c${statusCode}\nMessage: %c${message}\nError Data:`,
        "color: black; background-color: yellow; font-weight: bold;",
        "color: black; background-color: orange; font-weight: bold;",
        "color: black; background-color: red; font-weight: bold;",
        "color: white; background-color: red; font-weight: bold;",
        data,
      );

      throw apiError; // 전체 에러 객체 throw
    }

    console.error("🚨 Unexpected error making API request 🚨\n", error);
    throw error;
  }
};

export default api;
