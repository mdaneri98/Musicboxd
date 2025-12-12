/**
 * AlbumCard Component
 * Displays album information in a card format
 */

import Link from 'next/link';
import { Album } from '@/types';
import { imageRepository } from '@/repositories';

interface AlbumCardProps {
  album: Album;
}

const AlbumCard = ({ album }: AlbumCardProps) => {

  const albumImageUrl = album.image_id
    ? imageRepository.getImageUrl(album.image_id)
    : '/assets/image-placeholder.png';

  return (
    <div key={album.id} className="music-item album-item">
    <Link href={`/albums/${album.id}`} className="music-item-link">
      <div className="music-item-image-container">
        <img
          src={albumImageUrl}
          alt={album.title}
          className="music-item-image"
        />
        {album.avg_rating !== 0 && (
          <div className="rating-badge">
            <span className="rating">{album.avg_rating.toFixed(1)}</span>
            <span className="star">&#9733;</span>
          </div>
        )}
      </div>
      <p className="music-item-title">{album.title}</p>
    </Link>
  </div>
  );
};

export default AlbumCard;

