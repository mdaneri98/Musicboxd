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
    if (song.favorite) {
      await dispatch(removeSongFavoriteAsync(song.id));
    } else {
      await dispatch(addSongFavoriteAsync(song.id));
    }
  };

  return (
    <div className="song-card">
      <Link href={`/songs/${song.id}`} className="song-card-link">
        <div className="song-card-info">
          {song.track_number && (
            <span className="song-card-track">{song.track_number}</span>
          )}
          <div className="song-card-details">
            <h3 className="song-card-title">{song.title}</h3>
            <p className="song-card-subtitle">
               {song.album_title}
            </p>
          </div>
          <span className="song-card-duration">{(song.duration)}</span>
          {song.avg_rating && (
            <div className="song-card-rating">
              <span className="star filled">&#9733;</span>
              <span className="rating-value">{song.avg_rating.toFixed(1)}</span>
            </div>
          )}
        </div>
      </Link>
      <button
        onClick={handleFavorite}
        className={`favorite-btn ${song.favorite ? 'active' : ''}`}
        title={song.favorite ? 'Remove from favorites' : 'Add to favorites'}
      >
        <i className={`fa-${song.favorite ? 'solid' : 'regular'} fa-heart`}></i>
      </button>
    </div>
  );
};

export default SongCard;

