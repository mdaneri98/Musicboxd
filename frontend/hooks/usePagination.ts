import { useState, useCallback } from 'react';
import { useRouter } from 'next/router';

/**
 * Custom hook for pagination state management
 * Supports URL synchronization for better UX
 * 
 * Best Practice: Encapsulates pagination logic in a reusable hook,
 * with URL sync for shareable links and browser navigation
 * 
 * @param options - Configuration options
 * @returns Object with pagination state and handlers
 * 
 * @example
 * const { currentPage, pageSize, goToPage, nextPage, prevPage } = usePagination({
 *   initialPage: 0,
 *   pageSize: 20,
 *   syncWithUrl: true,
 * });
 */
interface UsePaginationOptions {
  initialPage?: number;
  pageSize?: number;
  syncWithUrl?: boolean;
  paramName?: string;
}

export function usePagination(options: UsePaginationOptions = {}) {
  const {
    initialPage = 0,
    pageSize: initialPageSize = 20,
    syncWithUrl = false,
    paramName = 'page',
  } = options;

  const router = useRouter();

  // Get initial page from URL if sync is enabled
  const getInitialPage = () => {
    if (syncWithUrl && router.query[paramName]) {
      const urlPage = parseInt(router.query[paramName] as string, 10);
      return isNaN(urlPage) ? initialPage : urlPage;
    }
    return initialPage;
  };

  const [currentPage, setCurrentPage] = useState(getInitialPage);
  const [pageSize, setPageSize] = useState(initialPageSize);

  // Update URL when page changes
  const updateUrl = useCallback(
    (page: number) => {
      if (syncWithUrl) {
        router.push(
          {
            pathname: router.pathname,
            query: { ...router.query, [paramName]: page },
          },
          undefined,
          { shallow: true }
        );
      }
    },
    [router, syncWithUrl, paramName]
  );

  const goToPage = useCallback(
    (page: number) => {
      const newPage = Math.max(0, page);
      setCurrentPage(newPage);
      updateUrl(newPage);
    },
    [updateUrl]
  );

  const nextPage = useCallback(() => {
    goToPage(currentPage + 1);
  }, [currentPage, goToPage]);

  const prevPage = useCallback(() => {
    goToPage(currentPage - 1);
  }, [currentPage, goToPage]);

  const reset = useCallback(() => {
    goToPage(initialPage);
  }, [initialPage, goToPage]);

  const changePageSize = useCallback(
    (newSize: number) => {
      setPageSize(newSize);
      goToPage(0); // Reset to first page when page size changes
    },
    [goToPage]
  );

  return {
    currentPage,
    pageSize,
    goToPage,
    nextPage,
    prevPage,
    reset,
    changePageSize,
    // Computed values
    offset: currentPage * pageSize,
    canGoPrev: currentPage > 0,
  };
}

