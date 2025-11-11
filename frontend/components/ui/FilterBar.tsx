import { FilterTypeEnum } from '@/types';

/**
 * Filter Bar Component
 * 
 * Best Practice: Reusable filter dropdown with type-safe options
 */

interface FilterOption {
  value: FilterTypeEnum;
  label: string;
}

interface FilterBarProps {
  value: FilterTypeEnum;
  onChange: (filter: FilterTypeEnum) => void;
  options?: FilterOption[];
  label?: string;
  className?: string;
}

const defaultOptions: FilterOption[] = [
  { value: FilterTypeEnum.POPULAR, label: 'Popular' },
  { value: FilterTypeEnum.RECENT, label: 'Most Recent' },
  { value: FilterTypeEnum.RATING, label: 'Rating' },
  { value: FilterTypeEnum.LIKES, label: 'Likes' },
  { value: FilterTypeEnum.FIRST, label: 'First' },
  { value: FilterTypeEnum.NEWEST, label: 'Newest' },
  { value: FilterTypeEnum.OLDEST, label: 'Oldest' },
];

export function FilterBar({
  value,
  onChange,
  options = defaultOptions,
  label = 'Sort by:',
  className = '',
}: FilterBarProps) {
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    onChange(e.target.value as FilterTypeEnum);
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

