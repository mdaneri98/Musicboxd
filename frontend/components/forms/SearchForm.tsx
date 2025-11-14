/**
 * SearchForm Component
 * Global search form with type filter
 */

import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { searchSchema } from '@/utils/validationSchemas';
import { SearchFormData } from '@/types';
import { SearchType } from '@/types/enums';

interface SearchFormProps {
  onSubmit: (data: SearchFormData) => void;
  defaultValues?: Partial<SearchFormData>;
  isLoading?: boolean;
}

const SearchForm = ({
  onSubmit,
  defaultValues = { type: SearchType.MUSIC },
  isLoading,
}: SearchFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SearchFormData>({
    resolver: yupResolver(searchSchema) as any,
    defaultValues,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="search-form">
      <div className="search-input-group">
        <input
          type="text"
          {...register('query')}
          className="search-input"
          placeholder="Search for music, artists, users..."
        />
        <select {...register('type')} className="search-select">
          <option value="all">All</option>
          <option value="users">Users</option>
          <option value="artists">Artists</option>
          <option value="albums">Albums</option>
          <option value="songs">Songs</option>
          <option value="reviews">Reviews</option>
        </select>
        <button
          type="submit"
          className="btn btn-primary"
          disabled={isLoading}
        >
          <i className="fas fa-search"></i>
        </button>
      </div>
      {errors.query && <p className="form-error">{errors.query.message}</p>}
    </form>
  );
};

export default SearchForm;

