/**
 * Auth Provider
 * Initializes authentication state from localStorage on app mount
 * Similar to AuthContext pattern but using Redux
 */

import { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { checkAuthAsync, selectAuthInitializing } from '@/store/slices/authSlice';

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const dispatch = useAppDispatch();
  const initializing = useAppSelector(selectAuthInitializing);

  useEffect(() => {
    // Check auth status on mount (restore from localStorage)
    dispatch(checkAuthAsync());
  }, [dispatch]);

  // Show loading state while initializing
  if (initializing) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};

export default AuthProvider;

