import Link from 'next/link';
import { Artist, User } from '@/types';
import { RatingCard } from '@/components/ui';
import { imageRepository } from '@/repositories';
import { useTranslation } from 'react-i18next';
interface ArtistInfoProps {
  artist: Artist;
  currentUser: User | null;
  isAuthenticated: boolean;
  isFavorite: boolean;
  favoriteLoading: boolean;
  userRating?: number;
  isReviewed: boolean;
  onFavoriteToggle: () => void;
}

export const ArtistInfo: React.FC<ArtistInfoProps> = ({
  artist,
  currentUser,
  isAuthenticated,
  isFavorite,
  favoriteLoading,
  userRating,
  isReviewed,
  onFavoriteToggle,
}) => {
  const artistImgUrl = artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png';
  const { t } = useTranslation();
  return (
    <>
      {/* Artist Header */}
      <section className="entity-header">
        <div className="entity-main-info">
          <img src={artistImgUrl} alt={artist.name} className="entity-image" />
          <div className="entity-details">
            <div className="entity-type">
              Artist
              {currentUser && currentUser.moderator && (
                <Link href={`/moderator/music?artistId=${artist.id}`} className="edit-link">
                  <i className="fas fa-pencil-alt"></i>
                </Link>
              )}
            </div>
            <h1 className="entity-title">{artist.name}</h1>
            {artist.bio && <p className="entity-description">{artist.bio}</p>}
          </div>
        </div>

        {/* Rating Card */}
        <div className="rating-card-container">
          <RatingCard
            totalRatings={artist.rating_count || 0}
            averageRating={artist.avg_rating || 0}
            userRating={userRating}
            reviewed={isReviewed}
            entityType="artists"
            entityId={artist.id}
            entityLabel="artist"
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

