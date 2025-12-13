import dynamic from 'next/dynamic';


export const ReviewCard = dynamic(() => import('./ReviewCard'), {
  ssr: false
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

export const SongCard = dynamic(() => import('./SongCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});

export const AlbumCard = dynamic(() => import('./AlbumCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});

export const ArtistCard = dynamic(() => import('./ArtistCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});

export const UserCard = dynamic(() => import('./UserCard'), {
  loading: () => {
    const { LoadingSpinner } = require('@/components/ui/LoadingSpinner');
    return <div className="card-skeleton"><LoadingSpinner size="small" /></div>;
  },
});
