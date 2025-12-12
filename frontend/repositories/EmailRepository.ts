import { apiClient } from '@/lib/apiClient';
import {
  EmailVerificationRequest,
  ResendVerificationRequest,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const EMAIL_ENDPOINTS = {
  VERIFY_EMAIL: '/email/verification',
  RESEND_VERIFICATION: '/email/verification/resend',
};

// ============================================================================
// Email Repository Class
// ============================================================================

class EmailRepository {
  /**
   * Verify email address with verification code
   * Backend returns 204 No Content on success, throws exception on failure
   * @param code Verification code from email
   * @returns void - throws error if verification fails
   */
  async verifyEmail(code: string): Promise<void> {
    try {
      const request: EmailVerificationRequest = { code };

      await apiClient.post<void>(
        EMAIL_ENDPOINTS.VERIFY_EMAIL,
        request
      );
    } catch (error) {
      console.error('Email verification error:', error);
      throw error;
    }
  }

  /**
   * Resend verification email to user
   * Backend returns 204 No Content on success, throws exception on failure
   * @param email User's email address
   * @returns void - throws error if resend fails
   */
  async resendVerification(email: string): Promise<void> {
    try {
      const request: ResendVerificationRequest = { email };

      await apiClient.post<void>(
        EMAIL_ENDPOINTS.RESEND_VERIFICATION,
        request
      );
    } catch (error) {
      console.error('Resend verification error:', error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const emailRepository = new EmailRepository();
