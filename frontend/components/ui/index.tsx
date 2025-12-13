import dynamic from 'next/dynamic';

export { Button } from './Button';
export { LoadingSpinner } from './LoadingSpinner';
export { ErrorMessage } from './ErrorMessage';
export { SuccessMessage } from './SuccessMessage';
export { Pagination } from './Pagination';
export { FollowButton } from './FollowButton';
export { LikeButton } from './LikeButton';
export { SearchBar } from './SearchBar';
export { default as RatingDisplay } from './RatingDisplay';
export { default as RatingCard } from './RatingCard';
export { default as LanguageSwitcher } from './LanguageSwitcher';

export const Modal = dynamic(() => import('./Modal').then(mod => ({ default: mod.Modal })), {
  ssr: false,
});

export const ConfirmationModal = dynamic(() => import('./ConfirmationModal'), {
  ssr: false,
});

export const ImageUpload = dynamic(
  () => import('./ImageUpload').then(mod => ({ default: mod.ImageUpload })),
  {
    loading: () => {
      const { LoadingSpinner } = require('./LoadingSpinner');
      return <LoadingSpinner size="small" />;
    },
  }
);

