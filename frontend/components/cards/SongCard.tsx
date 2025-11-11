/**
 * SongCard Component
 * Displays song information in a card format
 */

import Link from 'next/link';
import { Song } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import {
  addSongFavoriteAsync,
  removeSongFavoriteAsync,
} from '@/store/slices';

interface SongCardProps {
  song: Song;
}

const SongCard = ({ song }: SongCardProps) => {
  const dispatch = useAppDispatch();

  const handleFavorite = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (song.isFavorite) {
      await dispatch(removeSongFavoriteAsync(song.id));
    } else {
      await dispatch(addSongFavoriteAsync(song.id));
    }
  };

  const formatDuration = (seconds?: number) => {
    if (!seconds) return '--:--';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="song-card">
      <Link href={`/songs/${song.id}`} className="song-card-link">
        <div className="song-card-info">
          {song.trackNumber && (
            <span className="song-card-track">{song.trackNumber}</span>
          )}
          <div className="song-card-details">
            <h3 className="song-card-title">{song.title}</h3>
            <p className="song-card-subtitle">
              {song.artistName} • {song.albumTitle}
            </p>
          </div>
          <span className="song-card-duration">{formatDuration(song.duration)}</span>
          {song.averageRating && (
            <div className="song-card-rating">
              <span className="star filled">&#9733;</span>
              <span className="rating-value">{song.averageRating.toFixed(1)}</span>
            </div>
          )}
        </div>
      </Link>
      <button
        onClick={handleFavorite}
        className={`favorite-btn ${song.isFavorite ? 'active' : ''}`}
        title={song.isFavorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        <i className={`fa-${song.isFavorite ? 'solid' : 'regular'} fa-heart`}></i>
      </button>
    </div>
  );
};

export default SongCard;

