import { useState } from 'react';
import { useAppDispatch } from '@/store/hooks';
import {
  addArtistFavoriteAsync,
  removeArtistFavoriteAsync,
  addAlbumFavoriteAsync,
  removeAlbumFavoriteAsync,
  addSongFavoriteAsync,
  removeSongFavoriteAsync,
} from '@/store/slices';

/**
 * Favorite Button Component
 * Supports artists, albums, and songs
 * 
 * Best Practice: Single component with polymorphic behavior based on item type
 */

interface FavoriteButtonProps {
  itemId: number;
  itemType: 'artist' | 'album' | 'song';
  isFavorite: boolean;
  onFavoriteChange?: (isFavorite: boolean) => void;
  variant?: 'icon' | 'button';
  className?: string;
}

export function FavoriteButton({
  itemId,
  itemType,
  isFavorite: initialIsFavorite,
  onFavoriteChange,
  variant = 'icon',
  className = '',
}: FavoriteButtonProps) {
  const dispatch = useAppDispatch();
  const [isFavorite, setIsFavorite] = useState(initialIsFavorite);
  const [isLoading, setIsLoading] = useState(false);

  const handleToggleFavorite = async () => {
    setIsLoading(true);
    const previousState = isFavorite;

    try {
      // Optimistic update
      setIsFavorite(!isFavorite);

      // Dispatch appropriate action based on item type
      if (isFavorite) {
        // Remove from favorites
        switch (itemType) {
          case 'artist':
            await dispatch(removeArtistFavoriteAsync(itemId)).unwrap();
            break;
          case 'album':
            await dispatch(removeAlbumFavoriteAsync(itemId)).unwrap();
            break;
          case 'song':
            await dispatch(removeSongFavoriteAsync(itemId)).unwrap();
            break;
        }
      } else {
        // Add to favorites
        switch (itemType) {
          case 'artist':
            await dispatch(addArtistFavoriteAsync(itemId)).unwrap();
            break;
          case 'album':
            await dispatch(addAlbumFavoriteAsync(itemId)).unwrap();
            break;
          case 'song':
            await dispatch(addSongFavoriteAsync(itemId)).unwrap();
            break;
        }
      }

      onFavoriteChange?.(!isFavorite);
    } catch (error) {
      // Revert on error
      setIsFavorite(previousState);
      console.error('Failed to toggle favorite:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (variant === 'icon') {
    return (
      <button
        onClick={handleToggleFavorite}
        disabled={isLoading}
        className={`favorite-btn favorite-icon ${isFavorite ? 'favorited' : ''} ${className}`}
        type="button"
        aria-label={isFavorite ? `Remove from favorites` : `Add to favorites`}
        title={isFavorite ? `Remove from favorites` : `Add to favorites`}
      >
        {isFavorite ? '★' : '☆'}
      </button>
    );
  }

  return (
    <button
      onClick={handleToggleFavorite}
      disabled={isLoading}
      className={`btn ${isFavorite ? 'btn-secondary' : 'btn-primary'} ${className}`}
      type="button"
    >
      {isLoading ? 'Loading...' : isFavorite ? '★ Favorited' : '☆ Add to Favorites'}
    </button>
  );
}

