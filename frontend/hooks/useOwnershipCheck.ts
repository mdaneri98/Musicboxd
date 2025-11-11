import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useAppSelector } from '@/store/hooks';
import { selectCurrentUser, selectIsAuthenticated } from '@/store/slices';

/**
 * Custom hook for checking resource ownership
 * Useful for edit pages and other owner-only actions
 * 
 * Best Practice: Separates ownership logic into a reusable hook,
 * making it easy to protect owner-specific functionality
 * 
 * @param resourceOwnerId - The ID of the resource owner
 * @param options - Configuration options
 * @returns Object with ownership status and utility functions
 */
export function useOwnershipCheck(
  resourceOwnerId: number | null | undefined,
  options: {
    redirectTo?: string;
    allowModerators?: boolean;
  } = {}
) {
  const { redirectTo = '/403', allowModerators = true } = options;
  const router = useRouter();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const [isChecking, setIsChecking] = useState(true);

  // Determine if user has access
  const isOwner = currentUser?.id === resourceOwnerId;
  const isModerator = currentUser?.isModerator || false;
  const hasAccess = isOwner || (allowModerators && isModerator);

  useEffect(() => {
    // Wait for user data to be available
    if (!isAuthenticated || !currentUser) {
      setIsChecking(true);
      return;
    }

    // Check ownership/access
    if (resourceOwnerId !== null && resourceOwnerId !== undefined) {
      setIsChecking(false);

      // Redirect if no access
      if (!hasAccess) {
        router.push(redirectTo);
      }
    }
  }, [isAuthenticated, currentUser, resourceOwnerId, hasAccess, redirectTo, router]);

  return {
    isOwner,
    isModerator,
    hasAccess,
    isChecking,
    currentUser,
  };
}

