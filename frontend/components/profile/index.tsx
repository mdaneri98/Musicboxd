import dynamic from 'next/dynamic';

// Lazy-load tab sections (only loaded when tab is active)
export const FavoritesSection = dynamic(() => import('./FavoritesSection'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="large" />;
  },
});

export const ReviewsSection = dynamic(() => import('./ReviewsSection'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <LoadingSpinner size="large" />;
  },
});
