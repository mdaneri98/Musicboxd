/**
 * ArtistCard Component
 * Displays artist information in a card format
 */

import Link from 'next/link';
import { Artist } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import { imageRepository } from '@/repositories';
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
    if (artist.favorite) {
      await dispatch(removeArtistFavoriteAsync(artist.id));
    } else {
      await dispatch(addArtistFavoriteAsync(artist.id));
    }
  };

  const artistImageUrl = artist.image_id
    ? imageRepository.getImageUrl(artist.image_id)
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
            {artist.rating_count} {artist.rating_count === 1 ? 'album' : 'albums'}
          </p>
          {artist.avg_rating !== 0 && (
            <div className="music-card-rating">
              <span className="star filled">&#9733;</span>
              <span className="rating-value">{artist.avg_rating.toFixed(1)}</span>
            </div>
          )}
        </div>
      </Link>
      <button
        onClick={handleFavorite}
        className={`favorite-btn ${artist.favorite ? 'active' : ''}`}
        title={artist.favorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        <i className={`fa-${artist.favorite ? 'solid' : 'regular'} fa-heart`}></i>
      </button>
    </div>
  );
};

export default ArtistCard;

