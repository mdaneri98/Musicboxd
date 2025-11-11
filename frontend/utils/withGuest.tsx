import { ComponentType, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectAuthLoading, checkAuthAsync } from '@/store/slices';

/**
 * Higher-Order Component (HOC) for guest-only pages
 * Redirects to home page if user is already authenticated
 * 
 * Best Practice: This HOC prevents authenticated users from accessing
 * guest-only pages like login and register
 * 
 * @param Component - The component to wrap
 * @param redirectTo - The path to redirect to if authenticated (default: '/')
 * @returns Wrapped component with guest protection
 */
export function withGuest<P extends object>(
  Component: ComponentType<P>,
  redirectTo: string = '/'
) {
  const GuestComponent = (props: P) => {
    const router = useRouter();
    const dispatch = useAppDispatch();
    const isAuthenticated = useAppSelector(selectIsAuthenticated);
    const authLoading = useAppSelector(selectAuthLoading);

    useEffect(() => {
      // Check authentication on mount
      dispatch(checkAuthAsync());
    }, [dispatch]);

    useEffect(() => {
      // Redirect if authenticated after loading is complete
      if (!authLoading && isAuthenticated) {
        // Check if there's a redirect query param
        const redirect = router.query.redirect as string;
        router.push(redirect || redirectTo);
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

    // Don't render component if authenticated
    if (isAuthenticated) {
      return null;
    }

    // Render the wrapped component if not authenticated
    return <Component {...props} />;
  };

  // Set display name for debugging
  GuestComponent.displayName = `withGuest(${Component.displayName || Component.name || 'Component'})`;

  return GuestComponent;
}

