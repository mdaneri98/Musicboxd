import { useEffect, useRef, useCallback, useState } from 'react';

interface UseInfiniteScrollOptions {
  onLoadMore: () => Promise<void>;
  hasMore: boolean;
  isLoading: boolean;
  enabled?: boolean;
  rootMargin?: string;
}

interface UseInfiniteScrollReturn {
  sentinelRef: React.RefObject<HTMLDivElement | null>;
  isFetchingMore: boolean;
}

export function useInfiniteScroll({
  onLoadMore,
  hasMore,
  isLoading,
  enabled = true,
  rootMargin = '200px',
}: UseInfiniteScrollOptions): UseInfiniteScrollReturn {
  const sentinelRef = useRef<HTMLDivElement>(null);
  const [isFetchingMore, setIsFetchingMore] = useState(false);
  const isFetchingRef = useRef(false);

  const handleLoadMore = useCallback(async () => {
    if (isFetchingRef.current || isLoading || !hasMore || !enabled) {
      return;
    }

    isFetchingRef.current = true;
    setIsFetchingMore(true);
    
    try {
      await onLoadMore();
    } catch (error) {
      console.error('Failed to load more items:', error);
    } finally {
      isFetchingRef.current = false;
      setIsFetchingMore(false);
    }
  }, [onLoadMore, isLoading, hasMore, enabled]);

  useEffect(() => {
    if (!enabled || !hasMore) return;

    const sentinel = sentinelRef.current;
    if (!sentinel) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          handleLoadMore();
        }
      },
      { rootMargin }
    );

    observer.observe(sentinel);

    return () => observer.disconnect();
  }, [enabled, hasMore, rootMargin, handleLoadMore]);

  return {
    sentinelRef,
    isFetchingMore,
  };
}

export default useInfiniteScroll;
