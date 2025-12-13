import dynamic from 'next/dynamic';


export const LoginForm = dynamic(() => import('./LoginForm'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="medium" />;
  },
});

export const RegisterForm = dynamic(() => import('./RegisterForm'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="medium" />;
  },
});

export const ReviewForm = dynamic(() => import('./ReviewForm'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="medium" />;
  },
});

export const EditProfileForm = dynamic(() => import('./EditProfileForm'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="medium" />;
  },
});

export const CommentForm = dynamic(() => import('./CommentForm'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="small" />;
  },
});

// Lightweight form - loaded normally
export { default as SearchForm } from './SearchForm';

