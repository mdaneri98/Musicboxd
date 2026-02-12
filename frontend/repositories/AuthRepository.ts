/**
 * Auth Repository
 * Handles authentication-related API calls
 */

import { apiClient, tokenStorage } from '@/lib/apiClient';
import { CustomMediaType } from '@/types/mediaTypes';
import {
  User,
  LoginResponse,
  HALResource,
} from '@/types';
import { RegisterFormData } from '@/types/forms';
import { jwtDecode } from 'jwt-decode';

// ============================================================================
// API Endpoints
// ============================================================================

const AUTH_ENDPOINTS = {
  USERS: '/users',
};

// ============================================================================
// Auth Repository Class
// ============================================================================

interface DecodedToken {
  userId: number;
  sub: string;
  roles: string;
  exp: number;
  iat: number;
  type?: string;
}

class AuthRepository {
  /**
   * Login with username and password using Basic Auth
   * @param username User username
   * @param password User password
   * @returns LoginResponse with tokens and user data
   */
  async login(username: string, password: string): Promise<LoginResponse> {
    try {
      const credentials = btoa(`${username}:${password}`);

      // Use a lightweight request to exchange credentials for tokens
      const response = await apiClient.getWithHeaders<any>(
        `${AUTH_ENDPOINTS.USERS}?page=1&size=0`,
        {
          headers: { 'Authorization': `Basic ${credentials}` }
        }
      );

      const accessToken = response.headers['x-jwt-token'];
      const refreshToken = response.headers['x-jwt-refresh-token'];

      if (!accessToken || !refreshToken) {
        throw new Error('Login failed: No tokens received in headers');
      }

      tokenStorage.setTokens(accessToken, refreshToken);

      // Decode token to get userId to fetch full profile
      const decoded = jwtDecode<DecodedToken>(accessToken);
      const userId = decoded.userId;

      // Fetch full user profile
      const userResponse = await apiClient.getResource<User>(`${AUTH_ENDPOINTS.USERS}/${userId}`);

      return {
        access_token: accessToken,
        refresh_token: refreshToken,
        user: userResponse
      };

    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  /**
   * Register a new user
   * @param registerData Registration data
   * @returns Created user (and logs in if possible)
   */
  async register(registerData: RegisterFormData): Promise<LoginResponse> {
    try {
      // We need the full response to check for tokens
      await apiClient.postResource<User>(
        AUTH_ENDPOINTS.USERS,
        registerData,
        { headers: { 'Content-Type': CustomMediaType.USER } }
      );


      return this.login(registerData.username, registerData.password);

    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  /**
   * Logout user (client-side only)
   */
  async logout(): Promise<void> {
    tokenStorage.clearTokens();
    return Promise.resolve();
  }

  /**
   * Get current authenticated user
   * @returns Current user data
   */
  async getCurrentUser(): Promise<HALResource<User>> {
    try {
      const token = tokenStorage.getAccessToken();
      if (!token) throw new Error('No access token found');

      const decoded = jwtDecode<DecodedToken>(token);
      const userId = decoded.userId;

      const response = await apiClient.getResource<User>(
        `${AUTH_ENDPOINTS.USERS}/${userId}`
      );

      return response;
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

