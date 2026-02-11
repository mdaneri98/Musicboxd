/**
 * HATEOAS/HAL Helpers
 * Utilities for working with HAL (Hypertext Application Language) responses
 */

import { HALResource, HALLink } from '@/types';
import { apiClient } from '@/lib/apiClient';

// ============================================================================
// Link Extraction
// ============================================================================

/**
 * Get a link by relation from HAL resource
 */
export const getLink = (resource: HALResource, rel: string): HALLink | null => {
  if (!resource._links) return null;
  return resource._links.find((link) => link.rel === rel) || null;
};

/**
 * Get link href by relation
 */
export const getLinkHref = (resource: HALResource, rel: string): string | null => {
  const link = getLink(resource, rel);
  return link?.href || null;
};

// /**
//  * Check if a link exists
//  */
// export const hasLink = (resource: HALResource, rel: string): boolean => {
//   return getLink(resource, rel) !== null;
// };

// // ============================================================================
// // Link Navigation
// // ============================================================================

/**
 * Follow a link by relation and make GET request
 */
export const followLink = async <T>(
  resource: HALResource<T>,
  rel: string
): Promise<HALResource<T> | null> => {
  const href = getLinkHref(resource, rel);
  if (!href) return null;

  try {
    return await apiClient.getResource<T>(href);
  } catch (error) {
    console.error(`Error following link '${rel}':`, error);
    throw error;
  }
};

// /**
//  * Follow multiple links in parallel
//  */
// export const followLinks = async <T>(
//   resource: HALResource,
//   rels: string[]
// ): Promise<Map<string, HALResource<T> | null>> => {
//   const results = new Map<string, HALResource<T> | null>();

//   const promises = rels.map(async (rel) => {
//     try {
//       const result = await followLink<T>(resource, rel);
//       return { rel, result };
//     } catch (error) {
//       console.error(`Error following link '${rel}':`, error);
//       return { rel, result: null };
//     }
//   });

//   const responses = await Promise.all(promises);

//   responses.forEach(({ rel, result }) => {
//     results.set(rel, result);
//   });

//   return results;
// };

// ============================================================================
// Template Expansion
// ============================================================================

// /**
//  * Expand a templated URL with parameters
//  * Simple implementation for basic URI templates
//  */
// export const expandTemplate = (template: string, params: Record<string, string | number>): string => {
//   let expanded = template;

//   Object.entries(params).forEach(([key, value]) => {
//     // Replace {key} with value
//     expanded = expanded.replace(`{${key}}`, String(value));
//     // Replace {?key} with ?key=value (query param)
//     expanded = expanded.replace(`{?${key}}`, `?${key}=${value}`);
//   });

//   // Clean up any remaining template variables
//   expanded = expanded.replace(/\{[^}]+\}/g, '');

//   return expanded;
// };

// /**
//  * Get and expand a templated link
//  */
// export const getExpandedLink = (
//   resource: HALResource,
//   rel: string,
//   params: Record<string, string | number>
// ): string | null => {
//   const link = getLink(resource, rel);
//   if (!link || !link.href) return null;

//   if (link.templated) {
//     return expandTemplate(link.href, params);
//   }

//   return link.href;
// };

// ============================================================================
// Pagination Helpers
// ============================================================================

// /**
//  * Get pagination links
//  */
// export interface PaginationLinks {
//   first?: string;
//   prev?: string;
//   self?: string;
//   next?: string;
//   last?: string;
// }

// export const getPaginationLinks = (resource: HALResource): PaginationLinks => {
//   return {
//     first: getLinkHref(resource, 'first') || undefined,
//     prev: getLinkHref(resource, 'prev') || undefined,
//     self: getLinkHref(resource, 'self') || undefined,
//     next: getLinkHref(resource, 'next') || undefined,
//     last: getLinkHref(resource, 'last') || undefined,
//   };
// };

// /**
//  * Check if there is a next page
//  */
// export const hasNextPage = (resource: HALResource): boolean => {
//   return hasLink(resource, 'next');
// };

// /**
//  * Check if there is a previous page
//  */
// export const hasPrevPage = (resource: HALResource): boolean => {
//   return hasLink(resource, 'prev');
// };

// /**
//  * Follow next page link
//  */
// export const followNextPage = async <T>(resource: HALResource): Promise<HALResource<T> | null> => {
//   return followLink<T>(resource, 'next');
// };

// /**
//  * Follow previous page link
//  */
// export const followPrevPage = async <T>(resource: HALResource): Promise<HALResource<T> | null> => {
//   return followLink<T>(resource, 'prev');
// };

// ============================================================================
// URL Helpers
// ============================================================================

/**
 * Build query string from params
 */
export const buildQueryString = (params: Record<string, string | number | boolean>): string => {
  const queryParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      queryParams.append(key, String(value));
    }
  });

  const query = queryParams.toString();
  return query ? `?${query}` : '';
};

/**
 * Combine URL with query params
 */
export const buildUrl = (
  baseUrl: string,
  params?: Record<string, string | number | boolean>
): string => {
  if (!params || Object.keys(params).length === 0) {
    return baseUrl;
  }

  const queryString = buildQueryString(params);
  return `${baseUrl}${queryString}`;
};

// ============================================================================
// Resource Helpers
// ============================================================================

// /**
//  * Extract self link from resource
//  */
// export const getSelfLink = (resource: HALResource): string | null => {
//   return getLinkHref(resource, 'self');
// };

/**
 * Check if resource is valid (not null/undefined)
 */
export const hasData = <T>(resource: HALResource<T> | null | undefined): resource is HALResource<T> => {
  return resource !== null && resource !== undefined;
};

/**
 * Extract data from HAL resource (returns the resource itself as it is now flattened)
 */
export const extractData = <T>(resource: HALResource<T>): T | null => {
  return hasData(resource) ? resource : null;
};

/**
 * Extract data or throw error
 */
export const extractDataOrThrow = <T>(resource: HALResource<T>): T => {
  if (!hasData(resource)) {
    throw new Error('Resource is null or undefined');
  }
  return resource;
};

