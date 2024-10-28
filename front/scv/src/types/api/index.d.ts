import { InternalAxiosRequestConfig } from "axios";

export interface ApiRequestConfig extends InternalAxiosRequestConfig {
  bypassInterceptor?: boolean;
  _retry?: boolean;
}

export type HttpMethod = "get" | "post" | "put" | "delete" | "patch";
export type ConditionalData<M extends HttpMethod, D> = M extends
  | "get"
  | "delete"
  ? undefined
  : D;

export interface ApiResponse<T> {
  data: T;
  message: string;
  timestamp: string;
  statusCode: string;
}

export interface ApiErrorResponse {
  data: {
    code: string;
    trackingId: string;
    detailMessage: string;
  };
  message: string;
  timestamp: string;
  statusCode: string;
}
