import dynamic from 'next/dynamic';

export { default as UserCard } from './UserCard';
export { default as ArtistCard } from './ArtistCard';
export { default as AlbumCard } from './AlbumCard';
export { default as SongCard } from './SongCard';

export const ReviewCard = dynamic(() => import('./ReviewCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});

export const CommentCard = dynamic(() => import('./CommentCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});

export const NotificationCard = dynamic(() => import('./NotificationCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});

