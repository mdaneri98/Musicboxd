import { apiClient } from '@/lib/apiClient';
import { CustomMediaType } from '@/types/mediaTypes';
import {
  EmailVerificationRequest,
} from '@/types';

const EMAIL_ENDPOINTS = {
  VERIFY_EMAIL: (userId: number) => `/users/${userId}`,
};

class EmailRepository {
  async verifyEmail(userId: number, code: string): Promise<void> {
    try {
      const request: EmailVerificationRequest = { code };

      await apiClient.patch<void>(
        EMAIL_ENDPOINTS.VERIFY_EMAIL(userId),
        request,
        { headers: { 'Content-Type': CustomMediaType.USER_VERIFICATION } }
      );
    } catch (error) {
      console.error('Email verification error:', error);
      throw error;
    }
  }
}

export const emailRepository = new EmailRepository();
