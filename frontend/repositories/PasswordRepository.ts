import { apiClient } from '@/lib/apiClient';
import {
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const PASSWORD_ENDPOINTS = {
  FORGOT_PASSWORD: '/email/password/forgot',
  RESET_PASSWORD: '/email/password/reset',
};

// ============================================================================
// Password Repository Class
// ============================================================================

class PasswordRepository {
  /**
   * Request password reset email
   * Backend returns 204 No Content on success, throws exception on failure
   * @param email User's email address
   * @returns void - throws error if request fails
   */
  async forgotPassword(email: string): Promise<void> {
    try {
      const request: ForgotPasswordRequest = { email };

      await apiClient.post<void>(
        PASSWORD_ENDPOINTS.FORGOT_PASSWORD,
        request
      );
    } catch (error) {
      console.error('Forgot password error:', error);
      throw error;
    }
  }

  /**
   * Reset password with verification code
   * Backend returns 204 No Content on success, throws exception on failure
   * @param code Verification code from email
   * @param password New password
   * @param repeatPassword Password confirmation
   * @returns void - throws error if reset fails
   */
  async resetPassword(code: string, password: string, repeatPassword: string): Promise<void> {
    try {
      const request: ResetPasswordRequest = {
        code,
        password,
        repeat_password: repeatPassword, 
      };

      await apiClient.post<void>(
        PASSWORD_ENDPOINTS.RESET_PASSWORD,
        request
      );
    } catch (error) {
      console.error('Reset password error:', error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const passwordRepository = new PasswordRepository();
