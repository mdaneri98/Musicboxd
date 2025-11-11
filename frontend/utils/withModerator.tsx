import { ComponentType, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  selectAuthLoading,
  checkAuthAsync 
} from '@/store/slices';

/**
 * Higher-Order Component (HOC) for moderator-only pages
 * Redirects to 403 page if user is not a moderator
 * 
 * Best Practice: This HOC combines authentication and authorization checks,
 * ensuring only moderators can access protected admin pages
 * 
 * @param Component - The component to wrap
 * @returns Wrapped component with moderator protection
 */
export function withModerator<P extends object>(Component: ComponentType<P>) {
  const ModeratorComponent = (props: P) => {
    const router = useRouter();
    const dispatch = useAppDispatch();
    const isAuthenticated = useAppSelector(selectIsAuthenticated);
    const currentUser = useAppSelector(selectCurrentUser);
    const authLoading = useAppSelector(selectAuthLoading);

    useEffect(() => {
      // Check authentication on mount
      dispatch(checkAuthAsync());
    }, [dispatch]);

    useEffect(() => {
      // After loading is complete
      if (!authLoading) {
        // Redirect to login if not authenticated
        if (!isAuthenticated) {
          router.push(`/login?redirect=${encodeURIComponent(router.asPath)}`);
        }
        // Redirect to 403 if authenticated but not moderator
        else if (currentUser && !currentUser.isModerator) {
          router.push('/403');
        }
      }
    }, [isAuthenticated, currentUser, authLoading, router]);

    // Show loading state while checking authentication
    if (authLoading) {
      return (
        <div className="loading-container">
          <div className="loading-spinner">Loading...</div>
        </div>
      );
    }

    // Don't render component if not authenticated or not moderator
    if (!isAuthenticated || !currentUser || !currentUser.isModerator) {
      return null;
    }

    // Render the wrapped component if authenticated and moderator
    return <Component {...props} />;
  };

  // Set display name for debugging
  ModeratorComponent.displayName = `withModerator(${Component.displayName || Component.name || 'Component'})`;

  return ModeratorComponent;
}

