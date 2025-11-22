/**
 * SongCard Component
 * Displays song information in a card format
 */

import Link from 'next/link';
import { Song } from '@/types';

interface SongCardProps {
  song: Song;
  index: number;
}

const SongCard = ({ song, index }: SongCardProps) => {

  return (
    <li key={song.id}>
    <Link href={`/songs/${song.id}`} className="song-item">
      <span className="song-number">{index + 1}</span>
      <span className="song-title">{song.title}</span>
      {song.avg_rating !== 0 && (
        <div className="rating-badge">
          <span className="rating">{song.avg_rating.toFixed(1)}</span>
          <span className="star">&#9733;</span>
        </div>
      )}
    </Link>
  </li>
  );
};

export default SongCard;

