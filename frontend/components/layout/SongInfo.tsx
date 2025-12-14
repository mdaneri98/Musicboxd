import Link from 'next/link';
import { Song, Album, Artist, User } from '@/types';
import { RatingCard } from '@/components/ui';
import { imageRepository } from '@/repositories';
import { useTranslation } from 'react-i18next';
interface SongInfoProps {
  song: Song;
  album: Album | null;
  artist: Artist | null;
  currentUser: User | null;
  isAuthenticated: boolean;
  isFavorite: boolean;
  favoriteLoading: boolean;
  userRating?: number;
  isReviewed: boolean;
  onFavoriteToggle: () => void;
}

export const SongInfo: React.FC<SongInfoProps> = ({
  song,
  album,
  artist,
  currentUser,
  isAuthenticated,
  isFavorite,
  favoriteLoading,
  userRating,
  isReviewed,
  onFavoriteToggle,
}) => {
  const albumImgUrl = album?.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/image-placeholder.png';
  const artistImgUrl = artist?.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/image-placeholder.png';
  const { t } = useTranslation();
  return (
    <>
      {/* Song Header */}
      <section className="entity-header">
        <div className="entity-main-info">
          <img src={albumImgUrl} alt={song.title} className="entity-image album-cover" />
          <div className="entity-details">
            <div className="entity-type">
              {t('review.itemType.song').toUpperCase()}
              {currentUser && currentUser.moderator && (
                <Link href={`/moderator/music?artistId=${song.artist_id}&albumId=${song.album_id}&songId=${song.id}`} className="edit-link">
                  <i className="fas fa-pencil-alt"></i>
                </Link>
              )}
            </div>
            <h1 className="entity-title">{song.title}</h1>
            
            <div className="song-metadata">
              {/* Artist */}
              {artist && (
                <div className="artists-list">
                  <Link href={`/artists/${artist.id}`} className="artist-link">
                    <img src={artistImgUrl} alt={artist.name} className="artist-thumbnail" />
                    <span className="artist-name">{artist.name}</span>
                  </Link>
                </div>
              )}

              {/* Album */}
              {album && (
                <div className="album-link-container">
                  <Link href={`/albums/${album.id}`} className="album-link">
                    <img src={albumImgUrl} alt={album.title} className="album-thumbnail" />
                    <span className="album-name">{album.title}</span>
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Rating Card */}
        <div className="rating-card-container">
          <RatingCard
            totalRatings={song.rating_count || 0}
            averageRating={song.avg_rating || 0}
            userRating={userRating}
            reviewed={isReviewed}
            entityType="songs"
            entityId={song.id}
            entityLabel="song"
          />
        </div>
      </section>

      {/* Action Buttons */}
      <section className="entity-actions">
        {!isAuthenticated ? (
          <Link href="/login" className="btn btn-primary">
            {t("common.loginToAddFavorite")}
          </Link>
        ) : (
          <button
            onClick={onFavoriteToggle}
            disabled={favoriteLoading}
            className={`btn ${isFavorite ? 'btn-secondary' : 'btn-primary'}`}
          >
            {favoriteLoading
              ? t("common.loading")
              : isFavorite
              ? t("common.removeFromFavorites")
              : t("common.addToFavorites")}
          </button>
        )}
      </section>
    </>
  );
};

