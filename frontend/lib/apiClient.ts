/**
 * API Client
 * Axios-based HTTP client with JWT authentication and HATEOAS support
 */

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { HALResource, HALLink, APIError, APIRequestOptions, Collection } from '@/types';

// ============================================================================
// Configuration
// ============================================================================

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api';

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

    if ((error.response?.status === 401 || error.response?.status === 403) && !originalRequest._retry) {
      if (isRefreshing) {
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
        tokenStorage.clearTokens();
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }

      try {
        // Retry original request with refresh token
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${refreshToken}`;
        }

        const response = await axiosInstance(originalRequest);

        // Check for new tokens in headers
        const newAccessToken = response.headers['x-jwt-token'];
        const newRefreshToken = response.headers['x-jwt-refresh-token'];

        if (newAccessToken && newRefreshToken) {
          tokenStorage.setTokens(newAccessToken, newRefreshToken);
          processQueue(null, newAccessToken);
        }

        return response;
      } catch (refreshError) {
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
// Periodic Token Refresh (Module Level)
// ============================================================================



// Periodic refresh removed as it relies on specific endpoint.
// We allow the interceptor to handle refresh on 401s or 
// we could implement a proactive refresh using a safe GET endpoint.
// For now, we'll disable the interval to avoid errors.

if (typeof window !== 'undefined') {
  const win = window as any;
  if (win.__auth_refresh_interval) {
    clearInterval(win.__auth_refresh_interval);
  }
}

// ============================================================================
// Link Header Parser
// ============================================================================

interface ParsedPagination {
  currentPage: number;
  totalPages: number;
  pageSize: number;
  totalCount: number;
  links: HALLink[];
}

/**
 * Parse RFC 5988 Link headers into pagination metadata.
 * Example header: <http://host/api/artists?page=2&size=10>; rel="next", <...>; rel="last"
 */
const parseLinkHeader = (linkHeader: string): ParsedPagination => {
  const links: HALLink[] = [];
  let lastPage = 1;
  let currentPage = 1;
  let pageSize = 10;

  if (!linkHeader) {
    return { currentPage, totalPages: 1, pageSize, totalCount: 0, links };
  }

  // Split on comma, but be careful with commas inside angle brackets
  const parts = linkHeader.split(/,\s*(?=<)/);

  for (const part of parts) {
    const match = part.match(/<([^>]+)>;\s*rel="([^"]+)"/);
    if (!match) continue;

    const [, href, rel] = match;
    links.push({ href, rel });

    try {
      const url = new URL(href, 'http://localhost');
      const page = parseInt(url.searchParams.get('page') || '1');
      const size = parseInt(url.searchParams.get('size') || '10');

      if (size > 0) pageSize = size;

      if (rel === 'last') {
        lastPage = page;
      }
      if (rel === 'next') {
        currentPage = page - 1;
      }
      if (rel === 'prev') {
        currentPage = page + 1;
      }
      if (rel === 'first' && !links.some(l => l.rel === 'prev')) {
        // If no prev link, we might be on the first page
        // (will be overridden by next/prev if they exist)
      }
    } catch {
      // Invalid URL, skip
    }
  }

  // If there's no 'prev' and no 'next' that told us the page, assume page 1
  if (!links.some(l => l.rel === 'prev') && !links.some(l => l.rel === 'next')) {
    currentPage = 1;
  }

  const totalPages = lastPage;
  const totalCount = totalPages * pageSize;

  return { currentPage, totalPages, pageSize, totalCount, links };
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

  /**
   * GET collection - parses array body + Link headers into Collection<T>
   * Backend returns plain JSON arrays with RFC 5988 Link headers for pagination.
   */
  async getCollection<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<Collection<HALResource<T>>> {
    try {
      const response: AxiosResponse = await axiosInstance.get(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });

      // Handle 204 No Content (backend returns this when list is empty)
      if (response.status === 204 || !response.data) {
        const urlParams = new URLSearchParams(url.split('?')[1] || '');
        return {
          items: [],
          totalCount: 0,
          currentPage: parseInt(urlParams.get('page') || '1'),
          totalPages: 0,
          pageSize: parseInt(urlParams.get('size') || '10'),
          _links: [],
        };
      }

      // Backend returns a plain JSON array
      const items: HALResource<T>[] = Array.isArray(response.data) ? response.data : [];
      const linkHeader: string = response.headers?.['link'] || '';
      const pagination = parseLinkHeader(linkHeader);

      return {
        items,
        totalCount: pagination.totalCount > 0 ? pagination.totalCount : items.length,
        currentPage: pagination.currentPage,
        totalPages: pagination.totalPages,
        pageSize: pagination.pageSize,
        _links: pagination.links,
      };
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async getWithHeaders<T>(
    url: string,
    options?: APIRequestOptions
  ): Promise<{ data: T, headers: any }> {
    try {
      const response: AxiosResponse<T> = await axiosInstance.get(url, {
        headers: options?.headers,
        params: options?.params,
        signal: options?.signal,
      });
      return { data: response.data, headers: response.headers };
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

