import dynamic from 'next/dynamic';

export { LoadingSpinner } from './LoadingSpinner';
export { default as LanguageSwitcher } from './LanguageSwitcher';
export { ErrorMessage } from './ErrorMessage';



export const RatingCard = dynamic(() => import('./RatingCard'), {
  ssr: false,
});


export const ConfirmationModal = dynamic(() => import('./ConfirmationModal'), {
  ssr: false,
});

export const ToastContainer = dynamic(() => import('./ToastContainer'), {
  ssr: false,
});
