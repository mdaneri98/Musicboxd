import { useState, useEffect, InputHTMLAttributes } from 'react';
import { useDebounce } from '@/hooks';

/**
 * Search Bar Component with Debouncing
 * 
 * Best Practice: Implements debouncing to avoid excessive API calls
 */

interface SearchBarProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'onChange'> {
  onSearch: (query: string) => void;
  debounceDelay?: number;
  showClearButton?: boolean;
  className?: string;
}

export function SearchBar({
  onSearch,
  debounceDelay = 500,
  showClearButton = true,
  placeholder = 'Search...',
  className = '',
  ...props
}: SearchBarProps) {
  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, debounceDelay);

  // Call onSearch when debounced query changes
  useEffect(() => {
    onSearch(debouncedQuery);
  }, [debouncedQuery, onSearch]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setQuery(e.target.value);
  };

  const handleClear = () => {
    setQuery('');
    onSearch('');
  };

  return (
    <div className={`search-bar ${className}`}>
      <input
        type="text"
        value={query}
        onChange={handleChange}
        placeholder={placeholder}
        className="search-input"
        {...props}
      />
      {showClearButton && query && (
        <button
          onClick={handleClear}
          className="search-clear"
          type="button"
          aria-label="Clear search"
        >
          ×
        </button>
      )}
      <span className="search-icon">🔍</span>
    </div>
  );
}

