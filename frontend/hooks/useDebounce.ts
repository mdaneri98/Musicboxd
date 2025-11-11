import { useState, useEffect } from 'react';

/**
 * Custom hook for debouncing values
 * Useful for search inputs to avoid excessive API calls
 * 
 * Best Practice: Implements debouncing as a reusable hook,
 * following the separation of concerns principle
 * 
 * @param value - The value to debounce
 * @param delay - Delay in milliseconds (default: 500ms)
 * @returns Debounced value
 * 
 * @example
 * const [searchQuery, setSearchQuery] = useState('');
 * const debouncedQuery = useDebounce(searchQuery, 500);
 * 
 * useEffect(() => {
 *   if (debouncedQuery) {
 *     performSearch(debouncedQuery);
 *   }
 * }, [debouncedQuery]);
 */
export function useDebounce<T>(value: T, delay: number = 500): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    // Set up the timeout
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    // Clean up the timeout if value changes or component unmounts
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

