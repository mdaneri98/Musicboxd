/**
 * API Types
 * TypeScript interfaces for API responses (HATEOAS/HAL format)
 */

import { User } from '@/types/models';
import { FilterType } from './enums';

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

export interface FilterParams extends PaginationParams {
  filter?: FilterType;
  search?: string;
}

/**
 * Search parameters
 */
export interface SearchParams extends PaginationParams {
  search?: string;
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

