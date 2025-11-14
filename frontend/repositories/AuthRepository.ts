/**
 * Auth Repository
 * Handles authentication-related API calls
 */

import { apiClient, tokenStorage } from '@/lib/apiClient';
import {
  User,
  LoginCredentials,
  LoginResponse,
  RefreshTokenResponse,
  HALResource,
} from '@/types';
import { setCurrentUser } from '@/store/slices/authSlice';
import { RegisterFormData } from '@/types/forms';

// ============================================================================
// API Endpoints
// ============================================================================

const AUTH_ENDPOINTS = {
  LOGIN: '/auth/login',
  REGISTER: '/auth/register',
  REFRESH: '/auth/refresh',
  LOGOUT: '/auth/logout',
  CURRENT_USER: '/auth/me',
};

// ============================================================================
// Auth Repository Class
// ============================================================================

class AuthRepository {
  /**
   * Login with email and password
   * @param email User email
   * @param password User password
   * @returns LoginResponse with tokens and user data
   */
  async login(username: string, password: string): Promise<LoginResponse> {
    try {
      const credentials: LoginCredentials = { username, password };
      
      const response = await apiClient.post<LoginResponse>(
        AUTH_ENDPOINTS.LOGIN,
        credentials
      );

      if (!response) {
        throw new Error('Invalid login response: missing data');
      }

      const data = response as LoginResponse;

      if (!data.access_token || !data.refresh_token) {
        throw new Error('Invalid login response: missing data');
      }
      
      tokenStorage.setTokens(data.access_token, data.refresh_token);
      setCurrentUser(data.user);
    
      return data;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  /**
   * Register a new user
   * @param userData Registration data
   * @returns Created user
   */
  async register(registerData: RegisterFormData): Promise<HALResource<User>> {
    try {
      const response = await apiClient.postResource<User>(
        AUTH_ENDPOINTS.REGISTER,
        registerData
      );

      if (!response) {
        throw new Error('Invalid registration response: missing data');
      }

      return response as HALResource<User>;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  /**
   * Refresh access token using refresh token
   * @param refreshToken Refresh token
   * @returns New tokens
   */
  async refresh(refreshToken: string): Promise<RefreshTokenResponse> {
    try {
      const response = await apiClient.post<RefreshTokenResponse>(
        AUTH_ENDPOINTS.REFRESH,
        { refreshToken }
      );

      if (!response) {
        throw new Error('Invalid refresh response: missing data');
      }
      const refreshTokenResponse = response as RefreshTokenResponse;

      // Store new tokens
      tokenStorage.setTokens(refreshTokenResponse.access_token, refreshTokenResponse.refresh_token);

      return refreshTokenResponse;
    } catch (error) {
      console.error('Token refresh error:', error);
      // Clear tokens on refresh failure
      tokenStorage.clearTokens();
      throw error;
    }
  }

  /**
   * Logout user (invalidate refresh token on server)
   * @param refreshToken Refresh token to invalidate
   */
  async logout(refreshToken?: string): Promise<void> {
    try {
      const token = refreshToken || tokenStorage.getRefreshToken();

      if (token) {
        await apiClient.post(AUTH_ENDPOINTS.LOGOUT, { refreshToken: token });
      }
    } catch (error) {
      console.error('Logout error:', error);
      // Don't throw error on logout - we still want to clear tokens locally
    } finally {
      // Always clear tokens locally
      tokenStorage.clearTokens();
    }
  }

  /**
   * Get current authenticated user
   * @returns Current user data
   */
    async getCurrentUser(): Promise<HALResource<User>> {
    try {
      const response: HALResource<User> = await apiClient.getResource<User>(
        AUTH_ENDPOINTS.CURRENT_USER
      );

      if (!response) {
        throw new Error('Invalid user response: missing data');
      }

      return response as HALResource<User>;
    } catch (error) {
      console.error('Get current user error:', error);
      throw error;
    }
  }

  /**
   * Check if user is currently authenticated
   * @returns true if access token exists
   */
  isAuthenticated(): boolean {
    return tokenStorage.getAccessToken() !== null;
  }

  /**
   * Get stored access token
   * @returns Access token or null
   */
  getAccessToken(): string | null {
    return tokenStorage.getAccessToken();
  }

  /**
   * Get stored refresh token
   * @returns Refresh token or null
   */
  getRefreshToken(): string | null {
    return tokenStorage.getRefreshToken();
  }

  /**
   * Clear all authentication data
   */
  clearAuth(): void {
    tokenStorage.clearTokens();
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const authRepository = new AuthRepository();

