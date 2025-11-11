import { useCallback } from 'react';
import { HALResource } from '@/types';
import { getLinkHref, followLink } from '@/utils';

/**
 * Custom hook for navigating HATEOAS links
 * Provides utilities for working with HAL resources
 * 
 * Best Practice: Encapsulates HATEOAS navigation logic,
 * making it easier to work with hypermedia APIs
 * 
 * @returns Object with HATEOAS utility functions
 * 
 * @example
 * const { getLink, follow, hasLink } = useHATEOAS();
 * 
 * const userResource = await fetchUser(id);
 * if (hasLink(userResource, 'followers')) {
 *   const followersUrl = getLink(userResource, 'followers');
 *   const followers = await follow(userResource, 'followers');
 * }
 */
export function useHATEOAS() {
  /**
   * Get the href of a link by rel
   */
  const getLink = useCallback(<T>(resource: HALResource<T>, rel: string): string | null => {
    return getLinkHref(resource, rel);
  }, []);

  /**
   * Follow a link by rel and return the data
   */
  const follow = async <T>(resource: HALResource<T>, rel: string): Promise<HALResource<T> | null> => {
    return followLink<T>(resource, rel);
  };

  /**
   * Check if a resource has a specific link
   */
  const hasLink = useCallback(<T>(resource: HALResource<T>, rel: string): boolean => {
    return getLinkHref(resource, rel) !== null;
  }, []);

  /**
   * Get all links from a resource
   */
  const getAllLinks = useCallback(<T>(resource: HALResource<T>): Record<string, string> => {
    const links: Record<string, string> = {};
    if (resource._links) {
      Object.entries(resource._links).forEach(([rel, link]) => {
        if (typeof link === 'object' && 'href' in link) {
          links[rel] = link.href;
        }
      });
    }
    return links;
  }, []);

  return {
    getLink,
    follow,
    hasLink,
    getAllLinks,
  };
}

