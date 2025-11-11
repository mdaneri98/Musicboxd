import { FilterType } from '@/types';

/**
 * Filter Bar Component
 * 
 * Best Practice: Reusable filter dropdown with type-safe options
 */

interface FilterOption {
  value: FilterType;
  label: string;
}

interface FilterBarProps {
  value: FilterType;
  onChange: (filter: FilterType) => void;
  options?: FilterOption[];
  label?: string;
  className?: string;
}

const defaultOptions: FilterOption[] = [
  { value: 'POPULAR', label: 'Popular' },
  { value: 'RECENT', label: 'Most Recent' },
  { value: 'RATING', label: 'Rating' },
  { value: 'LIKES', label: 'Likes' },
  { value: 'FIRST', label: 'First' },
  { value: 'NEWEST', label: 'Newest' },
  { value: 'OLDEST', label: 'Oldest' },
];

export function FilterBar({
  value,
  onChange,
  options = defaultOptions,
  label = 'Sort by:',
  className = '',
}: FilterBarProps) {
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(e.target.value as FilterType);
  };

  return (
    <div className={`filter-bar ${className}`}>
      {label && (
        <label htmlFor="filter-select" className="filter-label">
          {label}
        </label>
      )}
      <select
        id="filter-select"
        value={value}
        onChange={handleChange}
        className="filter-select"
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}

