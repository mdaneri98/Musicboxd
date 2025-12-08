/**
 * SearchForm Component
 * Global search form with type filter
 */

import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { searchSchema } from '@/utils/validationSchemas';
import { SearchFormData } from '@/types';
import { SearchType } from '@/types/enums';
import { useTranslation } from 'react-i18next';

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
  const { t } = useTranslation();
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
          placeholder={t('search.placeholder')}
        />
        <select {...register('type')} className="search-select">
          <option value="all">{t('search.filters.all')}</option>
          <option value="users">{t('search.filters.users')}</option>
          <option value="artists">{t('search.filters.artists')}</option>
          <option value="albums">{t('search.filters.albums')}</option>
          <option value="songs">{t('search.filters.songs')}</option>
          <option value="reviews">{t('search.filters.reviews')}</option>
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

