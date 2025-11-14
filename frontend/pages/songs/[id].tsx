import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { RatingCard } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser,
  fetchSongByIdAsync,
  fetchAlbumByIdAsync,
  fetchArtistByIdAsync,
  fetchSongReviewsAsync,
  addSongFavoriteAsync,
  removeSongFavoriteAsync,
  selectCurrentSong,
  selectSongReviews,
  selectLoadingSong,
  selectSongError,
  clearCurrentSong
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { Album, Artist, Review, HALResource } from '@/types';

const SongDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const song = useAppSelector(selectCurrentSong);
  const reviews = useAppSelector(selectSongReviews);
  const loading = useAppSelector(selectLoadingSong);
  const error = useAppSelector(selectSongError);
  
  const [album, setAlbum] = useState<Album | null>(null);
  const [artist, setArtist] = useState<Artist | null>(null);
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();
  const [isReviewed, setIsReviewed] = useState(false);
  
  const isFavorite = song?.is_favorite || false;

  useEffect(() => {
    return () => {
      dispatch(clearCurrentSong());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!id) return;
    
    const songId = parseInt(id as string);
    dispatch(fetchSongByIdAsync(songId))
      .unwrap()
      .then((songData) => {
        dispatch(fetchAlbumByIdAsync(songData.data.album_id))
          .unwrap()
          .then((albumData) => setAlbum(albumData.data))
          .catch((err) => console.error('Failed to fetch album:', err));
        
        dispatch(fetchArtistByIdAsync(songData.data.artist_id))
          .unwrap()
          .then((artistData) => setArtist(artistData.data))
          .catch((err) => console.error('Failed to fetch artist:', err));
      })
      .catch((err) => console.error('Failed to fetch song:', err));
  }, [id, dispatch]);

  useEffect(() => {
    if (!id) return;
    
    const songId = parseInt(id as string);
    dispatch(fetchSongReviewsAsync({ songId, page: reviewsPage, size: 20 }))
      .unwrap()
      .then((reviewsData) => {
        setHasMoreReviews(reviewsData.items.length === 20);
        
        if (isAuthenticated && currentUser) {
          const userReview = reviewsData.items.find((r: HALResource<Review>) => r.data.user_id === currentUser.id);
          if (userReview) {
            setIsReviewed(true);
            setUserRating(userReview.data.rating || 0);
          }
        }
      })
      .catch((err) => console.error('Failed to fetch reviews:', err));
  }, [id, reviewsPage, isAuthenticated, currentUser, dispatch]);

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!song) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await dispatch(removeSongFavoriteAsync(song.id)).unwrap();
      } else {
        await dispatch(addSongFavoriteAsync(song.id)).unwrap();
      }
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  const formatDate = (date?: Date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading || !song) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading song...</div>
        </div>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout title="Error">
        <div className="content-wrapper">
          <div className="alert alert-danger" role="alert">
            <strong>Error:</strong> {error}
          </div>
        </div>
      </Layout>
    );
  }

  const albumImgUrl = album?.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png';
  const artistImgUrl = artist?.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png';

  return (
    <Layout title={`Musicboxd - ${song.title}`}>
      <div className="content-wrapper">
        {/* Song Header */}
        <section className="entity-header">
          <div className="entity-main-info">
            <img src={albumImgUrl} alt={song.title} className="entity-image album-cover" />
            <div className="entity-details">
              <div className="entity-type">
                Song
                {currentUser && currentUser.moderator && (
                  <Link href={`/mod/songs/${song.id}/edit`} className="edit-link">
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
              Login to Add Favorite
            </Link>
          ) : (
            <button
              onClick={handleFavoriteToggle}
              disabled={favoriteLoading}
              className={`btn ${isFavorite ? 'btn-secondary' : 'btn-primary'}`}
            >
              {favoriteLoading
                ? 'Loading...'
                : isFavorite
                ? 'Remove from Favorites'
                : 'Add to Favorites'}
            </button>
          )}
        </section>

        {/* Song Details */}
        <section className="entity-section song-details">
          <div className="song-info-grid">
            <div className="song-info-item">
              <span className="info-label">Duration:</span>
              <span className="info-value">{formatDuration(parseInt(song.duration))}</span>
            </div>
            {album?.genre && (
              <div className="song-info-item">
                <span className="info-label">Genre:</span>
                <span className="info-value">{album.genre}</span>
              </div>
            )}
            {album?.release_date && (
              <div className="song-info-item">
                <span className="info-label">Release Date:</span>
                <span className="info-value">{formatDate(album.release_date)}</span>
              </div>
            )}
          </div>
        </section>

        {/* Reviews Section */}
        {reviews.length > 0 && (
          <section className="entity-section">
            <h2>Reviews</h2>
            <div className="reviews-grid">
              {reviews.map((review) => (
                <ReviewCard key={review.id} review={review} />
              ))}
            </div>

            {/* Pagination */}
            <div className="pagination">
              {reviewsPage > 1 && (
                <button
                  onClick={() => setReviewsPage(reviewsPage - 1)}
                  className="btn btn-secondary"
                >
                  Previous Page
                </button>
              )}
              {hasMoreReviews && (
                <button
                  onClick={() => setReviewsPage(reviewsPage + 1)}
                  className="btn btn-secondary"
                >
                  Next Page
                </button>
              )}
            </div>
          </section>
        )}
      </div>
    </Layout>
  );
};

export default SongDetailPage;

