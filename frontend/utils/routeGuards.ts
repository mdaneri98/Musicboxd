import { NextRouter } from 'next/router';
import { User } from '@/types';

/**
 * Route Guards Utilities
 * 
 * Best Practice: Centralized route protection logic that can be used
 * in both client-side and server-side scenarios
 */

/**
 * Check if user is authenticated
 * @param user - Current user object
 * @returns True if authenticated
 */
export function requireAuth(user: User | null): boolean {
  return user !== null;
}

/**
 * Check if user is a guest (not authenticated)
 * @param user - Current user object
 * @returns True if guest
 */
export function requireGuest(user: User | null): boolean {
  return user === null;
}

/**
 * Check if user is a moderator
 * @param user - Current user object
 * @returns True if moderator
 */
export function requireModerator(user: User | null): boolean {
  return user !== null && user.isModerator;
}

/**
 * Check if user owns a resource
 * @param user - Current user object
 * @param resourceOwnerId - ID of the resource owner
 * @returns True if owner
 */
export function requireOwner(user: User | null, resourceOwnerId: number): boolean {
  return user !== null && user.id === resourceOwnerId;
}

/**
 * Check if user owns a resource OR is a moderator
 * @param user - Current user object
 * @param resourceOwnerId - ID of the resource owner
 * @returns True if owner or moderator
 */
export function requireOwnerOrModerator(user: User | null, resourceOwnerId: number): boolean {
  return requireOwner(user, resourceOwnerId) || requireModerator(user);
}

/**
 * Redirect helper with query params
 * @param router - Next.js router instance
 * @param path - Path to redirect to
 * @param currentPath - Current path (for redirect query param)
 */
export function redirectWithReturnUrl(
  router: NextRouter,
  path: string,
  currentPath?: string
): void {
  const returnUrl = currentPath || router.asPath;
  router.push(`${path}?redirect=${encodeURIComponent(returnUrl)}`);
}

/**
 * Get redirect URL from query params
 * @param router - Next.js router instance
 * @param fallback - Fallback URL if no redirect param
 * @returns Redirect URL
 */
export function getRedirectUrl(router: NextRouter, fallback: string = '/'): string {
  const redirect = router.query.redirect as string;
  return redirect || fallback;
}

/**
 * Route configuration type for protected routes
 */
export interface RouteConfig {
  path: string;
  requireAuth?: boolean;
  requireModerator?: boolean;
  requireOwnership?: boolean;
  redirectTo?: string;
}

/**
 * Common route configurations
 */
export const ROUTE_CONFIGS: Record<string, RouteConfig> = {
  // Public routes
  HOME: { path: '/', requireAuth: false },
  LANDING: { path: '/landing', requireAuth: false },
  SEARCH: { path: '/search', requireAuth: true },
  
  // Auth routes (guest only)
  LOGIN: { path: '/login', requireAuth: false },
  REGISTER: { path: '/register', requireAuth: false },
  
  // User routes (authenticated)
  PROFILE: { path: '/profile', requireAuth: true },
  PROFILE_EDIT: { path: '/profile/edit', requireAuth: true, requireOwnership: true },
  NOTIFICATIONS: { path: '/notifications', requireAuth: true },
  SETTINGS: { path: '/settings', requireAuth: true },
  
  // Moderator routes
  MODERATOR_DASHBOARD: { path: '/moderator', requireAuth: true, requireModerator: true },
  MODERATOR_ADD_ARTIST: { path: '/moderator/add-artist', requireAuth: true, requireModerator: true },
  MODERATOR_ADD_ALBUM: { path: '/moderator/add-album', requireAuth: true, requireModerator: true },
  MODERATOR_ADD_SONG: { path: '/moderator/add-song', requireAuth: true, requireModerator: true },
};

