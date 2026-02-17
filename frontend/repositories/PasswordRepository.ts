import { apiClient } from '@/lib/apiClient';
import { CustomMediaType } from '@/types/mediaTypes';
import {
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '@/types';

const PASSWORD_ENDPOINTS = {
  FORGOT_PASSWORD: '/users',
  RESET_PASSWORD: (userId: number) => `/users/${userId}`,
};

class PasswordRepository {
  async forgotPassword(email: string): Promise<void> {
    try {
      const request: ForgotPasswordRequest = { email };

      await apiClient.post<void>(
        PASSWORD_ENDPOINTS.FORGOT_PASSWORD,
        request,
        { headers: { 'Content-Type': CustomMediaType.USER_PASSWORD } }
      );
    } catch (error) {
      console.error('Forgot password error:', error);
      throw error;
    }
  }

  async resetPassword(userId: number, code: string, password: string, repeatPassword: string): Promise<void> {
    try {
      const request: ResetPasswordRequest = {
        code,
        password,
        repeat_password: repeatPassword,
      };

      await apiClient.patch<void>(
        PASSWORD_ENDPOINTS.RESET_PASSWORD(userId),
        request,
        { headers: { 'Content-Type': CustomMediaType.USER_PASSWORD } }
      );
    } catch (error) {
      console.error('Reset password error:', error);
      throw error;
    }
  }
}

export const passwordRepository = new PasswordRepository();
