/**
 * AlbumCard Component
 * Displays album information in a card format
 */

import Link from 'next/link';
import { Album } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import { imageRepository } from '@/repositories';
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
      if (album.is_favorite) {
      await dispatch(removeAlbumFavoriteAsync(album.id));
    } else {
      await dispatch(addAlbumFavoriteAsync(album.id));
    }
  };

  const albumImageUrl = album.image_id
    ? imageRepository.getImageUrl(album.image_id)
    : '/assets/default-album.png';

  return (
    <div className="music-card">
      <Link href={`/albums/${album.id}`} className="music-card-link">
        <div className="music-card-image">
          <img src={albumImageUrl} alt={album.title} className="img-cover" />
        </div>
        <div className="music-card-info">
          <h3 className="music-card-title">{album.title}</h3>
          <p className="music-card-subtitle">{album.artist_name}</p>
          {album.release_date && (
            <p className="music-card-date">
              {new Date(album.release_date).getFullYear()}
            </p>
          )}
          {album.avg_rating !== 0 && (
            <div className="music-card-rating">
              <span className="star filled">&#9733;</span>
              <span className="rating-value">{album.avg_rating.toFixed(1)}</span>
            </div>
          )}
        </div>
      </Link>
      <button
        onClick={handleFavorite}
        className={`favorite-btn ${album.is_favorite ? 'active' : ''}`}
        title={album.is_favorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        <i className={`fa-${album.is_favorite ? 'solid' : 'regular'} fa-heart`}></i>
      </button>
    </div>
  );
};

export default AlbumCard;

