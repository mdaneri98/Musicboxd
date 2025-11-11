import { useEffect, useCallback } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import {
  fetchUserByIdAsync,
  selectUserLoading,
  selectUserError,
} from '@/store/slices';

/**
 * Custom hook for fetching and managing user data
 * Automatically fetches user data if not in store
 * 
 * Best Practice: Encapsulates data fetching logic with caching,
 * reducing boilerplate in components
 * 
 * @param userId - The ID of the user to fetch
 * @param autoFetch - Whether to automatically fetch if not in store (default: true)
 * @returns Object with user data and loading state
 * 
 * @example
 * const { user, loading, error, refetch } = useUser(userId);
 * 
 * if (loading) return <LoadingSpinner />;
 * if (error) return <ErrorMessage message={error} />;
 * return <UserProfile user={user} />;
 */
export function useUser(userId: number | null | undefined, autoFetch: boolean = true) {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => (userId ? state.users.users[userId] || null : null));
  const loading = useAppSelector(selectUserLoading);
  const error = useAppSelector(selectUserError);

  useEffect(() => {
    // Fetch user if not in store and autoFetch is enabled
    if (autoFetch && userId && !user && !loading) {
      dispatch(fetchUserByIdAsync(userId));
    }
  }, [userId, user, loading, autoFetch, dispatch]);

  const refetch = useCallback(() => {
    if (userId) {
      dispatch(fetchUserByIdAsync(userId));
    }
  }, [userId, dispatch]);

  return {
    user,
    loading,
    error,
    refetch,
  };
}

