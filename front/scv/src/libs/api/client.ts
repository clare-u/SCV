import axios, { AxiosError } from "axios";
import {
  ApiErrorResponse,
  ApiRequestConfig,
  ApiResponse,
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
    const response = await api.request<ApiResponse<T>>({
      url,
      method,
      data,
      ...config,
    });
    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      const apiError: ApiErrorResponse = error.response.data;
      const { message, data, statusCode } = apiError;

      console.error(
        "🚨 API Request Error 🚨",
        `\nEndpoint: %c${url}`,
        "color: black; background-color: yellow; font-weight: bold;",
        `\nStatus Code: %c${statusCode}`,
        "color: black; background-color: orange; font-weight: bold;",
        `\nMessage: %c${message}`,
        "color: white; background-color: red; font-weight: bold;",
        `\nError Data:`,
        data,
      );

      throw apiError; // 전체 에러 객체 throw
    }

    console.error("🚨 Unexpected error making API request 🚨\n", error);
    throw error;
  }
};

export default api;
