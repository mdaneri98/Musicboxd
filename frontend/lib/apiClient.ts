/**
 * API Client
 * Axios-based HTTP client with JWT authentication and HATEOAS support
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { HALResource, APIError, APIRequestOptions, Collection } from '@/types';

// ============================================================================
// Configuration
// ============================================================================

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api_war/api';

// Token storage keys
const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

// ============================================================================
// Token Management
// ============================================================================

export const tokenStorage = {
  getAccessToken: (): string | null => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  setAccessToken: (token: string): void => {
    if (typeof window === 'undefined') return;
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
  },

  getRefreshToken: (): string | null => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  setRefreshToken: (token: string): void => {
    if (typeof window === 'undefined') return;
    localStorage.setItem(REFRESH_TOKEN_KEY, token);
  },

  clearTokens: (): void => {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },

  setTokens: (accessToken: string, refreshToken: string): void => {
    tokenStorage.setAccessToken(accessToken);
    tokenStorage.setRefreshToken(refreshToken);
  },
};

// ============================================================================
// Axios Instance
// ============================================================================

const axiosInstance: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ============================================================================
// Request Interceptor - Add Authorization Header
// ============================================================================

axiosInstance.interceptors.request.use(
  (config) => {
    const token = tokenStorage.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// ============================================================================
// Response Interceptor - Handle Token Refresh
// ============================================================================

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    // If error is 401 and we haven't retried yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // If already refreshing, queue this request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return axiosInstance(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = tokenStorage.getRefreshToken();

      if (!refreshToken) {
        // No refresh token, clear everything and redirect to login
        tokenStorage.clearTokens();
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }

      try {
        // Attempt to refresh token
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        const { accessToken, refreshToken: newRefreshToken } = response.data;

        tokenStorage.setTokens(accessToken, newRefreshToken);
        processQueue(null, accessToken);

        // Retry original request with new token
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Refresh failed, clear tokens and redirect
        processQueue(refreshError as Error, null);
        tokenStorage.clearTokens();
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

// ============================================================================
// Error Handler
// ============================================================================

const handleApiError = (error: unknown): APIError => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<APIError>;
    if (axiosError.response) {
      return {
        status: axiosError.response.status,
        message: axiosError.response.data?.message || axiosError.message,
        errors: axiosError.response.data?.errors,
        timestamp: axiosError.response.data?.timestamp,
        path: axiosError.response.data?.path,
      };
    }
    return {
      status: 0,
      message: axiosError.message || 'Network error',
    };
  }

  return {
    status: 0,
    message: 'An unexpected error occurred',
  };
};

// ============================================================================
// API Client Class
// ============================================================================

class ApiClient {
  /**
   * GET request
   */
  async getResource<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<HALResource<T>> {
    try {
      const response: AxiosResponse<HALResource<T>> = await axiosInstance.get(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async getCollection<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<Collection<HALResource<T>>> {
    try {
      const response: AxiosResponse<Collection<HALResource<T>>> = await axiosInstance.get(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  /**
   * POST request
   */
  async postResource<T, D = unknown>(
    url: string,
    data?: D,
    options?: APIRequestOptions
  ): Promise<HALResource<T>> {
    try {
      console.log("POST request to:", url);
      const response: AxiosResponse<HALResource<T>> = await axiosInstance.post(url, data, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  /**
   * PUT request
   */
  async putResource<T, D = unknown>(
    url: string,
    data?: D,
    options?: APIRequestOptions
  ): Promise<HALResource<T>> {
    try {
      const response: AxiosResponse<HALResource<T>> = await axiosInstance.put(url, data, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  /**
   * PATCH request
   */
  async patchResource<T, D = unknown>(
    url: string,
    data?: D,
    options?: APIRequestOptions
  ): Promise<HALResource<T>> {
    try {
      const response: AxiosResponse<HALResource<T>> = await axiosInstance.patch(url, data, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  /**
   * DELETE request
   */
  async deleteResource<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<HALResource<T>> {
    try {
      const response: AxiosResponse<HALResource<T>> = await axiosInstance.delete(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async get<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<T> {
    try {
      const response: AxiosResponse<T> = await axiosInstance.get(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async post<T, D = unknown>(
    url: string,
    data?: D,
    options?: APIRequestOptions
  ): Promise<T> {
    try {
      const response: AxiosResponse<T> = await axiosInstance.post(url, data, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async put<T, D = unknown>(
    url: string,
    data?: D,
    options?: APIRequestOptions
  ): Promise<T> {
    try {
      const response: AxiosResponse<T> = await axiosInstance.put(url, data, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,  
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async patch<T, D = unknown>(
    url: string,
    data?: D,
    options?: APIRequestOptions
  ): Promise<T> {
    try {
      const response: AxiosResponse<T> = await axiosInstance.patch(url, data, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async delete<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<T> {
    try {
      const response: AxiosResponse<T> = await axiosInstance.delete(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const apiClient = new ApiClient();

// ============================================================================
// Development Mode Logging
// ============================================================================

if (process.env.NODE_ENV === 'development') {
  axiosInstance.interceptors.request.use((config) => {
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  });

  axiosInstance.interceptors.response.use(
    (response) => {
      console.log(`[API Response] ${response.status} ${response.config.url}`);
      return response;
    },
    (error) => {
      console.error(
        `[API Error] ${error.response?.status || 'Network Error'} ${error.config?.url}`
      );
      return Promise.reject(error);
    }
  );
}

