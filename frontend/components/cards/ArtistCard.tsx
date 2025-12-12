/**
 * ArtistCard Component
 * Displays artist information in a card format
 */

import Link from 'next/link';
import { Artist } from '@/types';
import { imageRepository } from '@/repositories';

interface ArtistCardProps {
  artist: Artist;
}

const ArtistCard = ({ artist }: ArtistCardProps) => {

  const artistImageUrl = artist.image_id
    ? imageRepository.getImageUrl(artist.image_id)
    : '/assets/image-placeholder.png';

  return (
    <div key={artist.id} className="music-item artist-item">
    <Link href={`/artists/${artist.id}`} className="music-item-link">
      <div className="music-item-image-container">
        <img
          src={artistImageUrl}
          alt={artist.name}
          className="music-item-image"
        />
        {artist.avg_rating !== 0 && (
          <div className="rating-badge">
            <span className="rating">{artist.avg_rating.toFixed(1)}</span>
            <span className="star">&#9733;</span>
          </div>
        )}
      </div>
      <p className="music-item-title">{artist.name}</p>
    </Link>
  </div>
  );
};

export default ArtistCard;

