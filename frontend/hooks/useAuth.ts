import { useAppSelector } from '@/store/hooks';
import {
  selectCurrentUser,
  selectIsAuthenticated,
  selectAuthLoading,
  selectAuthError,
} from '@/store/slices';

/**
 * Custom hook for accessing authentication state
 * 
 * Best Practice: Encapsulates auth state access in a reusable hook,
 * making components cleaner and easier to test
 * 
 * @returns Object with auth state and user data
 */
export function useAuth() {
  const currentUser = useAppSelector(selectCurrentUser);
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const loading = useAppSelector(selectAuthLoading);
  const error = useAppSelector(selectAuthError);

  return {
    user: currentUser,
    isAuthenticated,
    isModerator: currentUser?.isModerator || false,
    isVerified: currentUser?.isVerified || false,
    loading,
    error,
  };
}

