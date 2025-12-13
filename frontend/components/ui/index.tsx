import dynamic from 'next/dynamic';

export { Button } from './Button';
export { LoadingSpinner } from './LoadingSpinner';
export { default as LanguageSwitcher } from './LanguageSwitcher';
export { ErrorMessage } from './ErrorMessage';
export { SuccessMessage } from './SuccessMessage';
export { Pagination } from './Pagination';
export { FollowButton } from './FollowButton';
export { LikeButton } from './LikeButton';
export { SearchBar } from './SearchBar';
export { default as RatingDisplay } from './RatingDisplay';


export const Modal = dynamic(() => import('./Modal').then(mod => ({ default: mod.Modal })), {
  ssr: false,
});

export const RatingCard = dynamic(() => import('./RatingCard'), {
  ssr: false,
});


export const ConfirmationModal = dynamic(() => import('./ConfirmationModal'), {
  ssr: false,
});

export const ToastContainer = dynamic(() => import('./ToastContainer'), {
  ssr: false,
});
