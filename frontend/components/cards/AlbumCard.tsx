/**
 * AlbumCard Component
 * Displays album information in a card format
 */

import Link from 'next/link';
import { Album } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import {
  addAlbumFavoriteAsync,
  removeAlbumFavoriteAsync,
} from '@/store/slices';

interface AlbumCardProps {
  album: Album;
}

const AlbumCard = ({ album }: AlbumCardProps) => {
  const dispatch = useAppDispatch();

  const handleFavorite = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (album.isFavorite) {
      await dispatch(removeAlbumFavoriteAsync(album.id));
    } else {
      await dispatch(addAlbumFavoriteAsync(album.id));
    }
  };

  const albumImageUrl = album.imageId
    ? `/api/images/${album.imageId}`
    : '/assets/default-album.png';

  return (
    <div className="music-card">
      <Link href={`/albums/${album.id}`} className="music-card-link">
        <div className="music-card-image">
          <img src={albumImageUrl} alt={album.title} className="img-cover" />
        </div>
        <div className="music-card-info">
          <h3 className="music-card-title">{album.title}</h3>
          <p className="music-card-subtitle">{album.artistName}</p>
          {album.releaseDate && (
            <p className="music-card-date">
              {new Date(album.releaseDate).getFullYear()}
            </p>
          )}
          {album.averageRating && (
            <div className="music-card-rating">
              <span className="star filled">&#9733;</span>
              <span className="rating-value">{album.averageRating.toFixed(1)}</span>
            </div>
          )}
        </div>
      </Link>
      <button
        onClick={handleFavorite}
        className={`favorite-btn ${album.isFavorite ? 'active' : ''}`}
        title={album.isFavorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        <i className={`fa-${album.isFavorite ? 'solid' : 'regular'} fa-heart`}></i>
      </button>
    </div>
  );
};

export default AlbumCard;

