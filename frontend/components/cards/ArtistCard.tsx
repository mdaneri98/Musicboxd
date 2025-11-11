/**
 * ArtistCard Component
 * Displays artist information in a card format
 */

import Link from 'next/link';
import { Artist } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import {
  addArtistFavoriteAsync,
  removeArtistFavoriteAsync,
} from '@/store/slices';

interface ArtistCardProps {
  artist: Artist;
}

const ArtistCard = ({ artist }: ArtistCardProps) => {
  const dispatch = useAppDispatch();

  const handleFavorite = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (artist.isFavorite) {
      await dispatch(removeArtistFavoriteAsync(artist.id));
    } else {
      await dispatch(addArtistFavoriteAsync(artist.id));
    }
  };

  const artistImageUrl = artist.imageId
    ? `/api/images/${artist.imageId}`
    : '/assets/default-artist.png';

  return (
    <div className="music-card">
      <Link href={`/artists/${artist.id}`} className="music-card-link">
        <div className="music-card-image">
          <img src={artistImageUrl} alt={artist.name} className="img-cover" />
        </div>
        <div className="music-card-info">
          <h3 className="music-card-title">{artist.name}</h3>
          <p className="music-card-subtitle">
            {artist.albumsCount} {artist.albumsCount === 1 ? 'album' : 'albums'}
          </p>
          {artist.averageRating && (
            <div className="music-card-rating">
              <span className="star filled">&#9733;</span>
              <span className="rating-value">{artist.averageRating.toFixed(1)}</span>
            </div>
          )}
        </div>
      </Link>
      <button
        onClick={handleFavorite}
        className={`favorite-btn ${artist.isFavorite ? 'active' : ''}`}
        title={artist.isFavorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        <i className={`fa-${artist.isFavorite ? 'solid' : 'regular'} fa-heart`}></i>
      </button>
    </div>
  );
};

export default ArtistCard;

