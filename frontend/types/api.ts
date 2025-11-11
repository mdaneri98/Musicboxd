/**
 * API Types
 * TypeScript interfaces for API responses (HATEOAS/HAL format)
 */

import { User } from '@/types/models';

// ============================================================================
// HAL (Hypertext Application Language) Types
// ============================================================================

/**
 * HAL Link object
 */
export interface HALLink {
  href: string;
  type?: string;
  title?: string;
  rel?: string;
  method?: string;
}

/**
 * Generic HAL Resource wrapper
 * All API responses follow this structure
 */
export interface HALResource<T = unknown> {
  data: T; // Main data payload
  _links: HALLink[];
}

// ============================================================================
// Pagination Types
// ============================================================================

/**
 * Paginated collection response
 */
export interface Collection<T> {
  items: T[];
  totalCount: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  _links: HALLink[];
}

/**
 * Pagination parameters for API requests
 */
export interface PaginationParams {
  page?: number;
  size?: number;
}

/**
 * Search parameters
 */
export interface SearchParams extends PaginationParams {
  search?: string;
}

/**
 * Filter parameters
 */
export type FilterType = "POPULAR" | "RECENT" | "OLDEST" | "LIKES" | "NEWEST" | "FIRST" | "RATING" | "USERNAME" | "EMAIL" | "NAME" | "BIO" | "IMAGE_ID" | "FOLLOWERS_AMOUNT" | "FOLLOWING_AMOUNT" | "REVIEW_AMOUNT" | "UPDATED_AT";

export interface FilterParams extends SearchParams {
  filter?: FilterType;
}

// ============================================================================
// Authentication Types
// ============================================================================

/**
 * Login credentials
 */
export interface LoginCredentials {
  username: string; // Backend uses username for login
  password: string;
}

/**
 * Login response with tokens
 */
export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  user: User;
}

/**
 * Registration data
 */
export interface RegisterData {
  username: string;
  email: string;
  password: string;
}

/**
 * Token refresh response
 */
export interface RefreshTokenResponse {
  access_token: string;
  refresh_token: string;
  user: User;
}

// ============================================================================
// Error Types
// ============================================================================

/**
 * API Error response
 */
export interface APIError {
  status: number;
  message: string;
  errors?: Record<string, string[]>; // Field validation errors
  timestamp?: string;
  path?: string;
}

/**
 * Field validation error
 */
export interface ValidationError {
  field: string;
  message: string;
}

// ============================================================================
// Request/Response Helpers
// ============================================================================

/**
 * Generic API response wrapper
 */
export type APIResponse<T> = HALResource<T>;

/**
 * API request options
 */
export interface APIRequestOptions {
  headers?: Record<string, string>;
  params?: Record<string, string | number | boolean>;
  signal?: AbortSignal;
}

