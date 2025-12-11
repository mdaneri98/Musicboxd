import Link from 'next/link';
import { Album, Artist, User } from '@/types';
import { RatingCard } from '@/components/ui';
import { imageRepository } from '@/repositories';
import { formatDate } from '@/utils/timeUtils';
import { useTranslation } from 'react-i18next';
interface AlbumInfoProps {
  album: Album;
  artist: Artist | null;
  currentUser: User | null;
  isAuthenticated: boolean;
  isFavorite: boolean;
  favoriteLoading: boolean;
  userRating?: number;
  isReviewed: boolean;
  onFavoriteToggle: () => void;
}

export const AlbumInfo: React.FC<AlbumInfoProps> = ({
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
  const albumImgUrl = album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png';
  const artistImgUrl = artist?.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png';
  const { t } = useTranslation();
  return (
    <>
      {/* Album Header */}
      <section className="entity-header">
        <div className="entity-main-info">
          <img src={albumImgUrl} alt={album.title} className="entity-image album-cover" />
          <div className="entity-details">
            <div className="entity-type">
              Album
              {currentUser && currentUser.moderator && (
                <Link href={`/moderator/music?artistId=${album.artist_id}&albumId=${album.id}`} className="edit-link">
                  <i className="fas fa-pencil-alt"></i>
                </Link>
              )}
            </div>
            <h1 className="entity-title">{album.title}</h1>
            
            <div className="album-metadata">
              {artist && (
                <Link href={`/artists/${artist.id}`} className="artist-link">
                  <img src={artistImgUrl} alt={artist.name} className="artist-thumbnail" />
                  <span className="artist-name">{artist.name}</span>
                </Link>
              )}
              <div className="album-info">
                {album.genre && <span className="album-genre">{album.genre}</span>}
                {album.genre && album.release_date && <span className="info-separator">&bull;</span>}
                {album.release_date && <span className="album-date">{formatDate(album.release_date)}</span>}
              </div>
            </div>
          </div>
        </div>

        {/* Rating Card */}
        <div className="rating-card-container">
          <RatingCard
            totalRatings={album.rating_count || 0}
            averageRating={album.avg_rating || 0}
            userRating={userRating}
            reviewed={isReviewed}
            entityType="albums"
            entityId={album.id}
            entityLabel="album"
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

