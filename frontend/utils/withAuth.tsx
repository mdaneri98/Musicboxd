import { ComponentType, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectAuthLoading, checkAuthAsync } from '@/store/slices';

/**
 * Higher-Order Component (HOC) for requiring authentication
 * Redirects to login page if user is not authenticated
 * 
 * Best Practice: This HOC handles authentication checks at the component level,
 * ensuring protected pages are only accessible to authenticated users
 * 
 * @param Component - The component to wrap
 * @param redirectTo - The path to redirect to if not authenticated (default: '/login')
 * @returns Wrapped component with authentication protection
 */
export function withAuth<P extends object>(
  Component: ComponentType<P>,
  redirectTo: string = '/login'
) {
  const AuthenticatedComponent = (props: P) => {
    const router = useRouter();
    const dispatch = useAppDispatch();
    const isAuthenticated = useAppSelector(selectIsAuthenticated);
    const authLoading = useAppSelector(selectAuthLoading);

    useEffect(() => {
      // Check authentication on mount
      dispatch(checkAuthAsync());
    }, [dispatch]);

    useEffect(() => {
      // Redirect if not authenticated after loading is complete
      if (!authLoading && !isAuthenticated) {
        router.push(`${redirectTo}?redirect=${encodeURIComponent(router.asPath)}`);
      }
    }, [isAuthenticated, authLoading, router]);

    // Show loading state while checking authentication
    if (authLoading) {
      return (
        <div className="loading-container">
          <div className="loading-spinner">Loading...</div>
        </div>
      );
    }

    // Don't render component if not authenticated
    if (!isAuthenticated) {
      return null;
    }

    // Render the wrapped component if authenticated
    return <Component {...props} />;
  };

  // Set display name for debugging
  AuthenticatedComponent.displayName = `withAuth(${Component.displayName || Component.name || 'Component'})`;

  return AuthenticatedComponent;
}

