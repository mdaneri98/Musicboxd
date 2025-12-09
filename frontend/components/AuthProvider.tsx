/**
 * Auth Provider
 * Initializes authentication state from localStorage on app mount
 * Similar to AuthContext pattern but using Redux
 */

import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { checkAuthAsync, selectAuthInitializing, selectCurrentUser } from '@/store/slices/authSlice';
import { LoadingSpinner } from './ui';

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const dispatch = useAppDispatch();
  const { i18n, t  } = useTranslation();
  const initializing = useAppSelector(selectAuthInitializing);
  const currentUser = useAppSelector(selectCurrentUser);

  useEffect(() => {
    dispatch(checkAuthAsync());
  }, [dispatch]);

  useEffect(() => {
    if (currentUser?.preferred_language && i18n.language !== currentUser.preferred_language) {
      i18n.changeLanguage(currentUser.preferred_language);
    }
  }, [currentUser?.preferred_language, i18n]);

  // Show loading state while initializing
  if (initializing) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <LoadingSpinner size="large" />
          <p className="mt-4 text-gray-600">{t("common.loading")}</p>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};

export default AuthProvider;

