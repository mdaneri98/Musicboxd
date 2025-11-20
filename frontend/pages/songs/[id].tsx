import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { Layout, SongInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
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

  return (
    <Layout title={`Musicboxd - ${song.title}`}>
      <div className="content-wrapper">
        <SongInfo
          song={song}
          album={album}
          artist={artist}
          currentUser={currentUser}
          isAuthenticated={isAuthenticated}
          isFavorite={isFavorite}
          favoriteLoading={favoriteLoading}
          userRating={userRating}
          isReviewed={isReviewed}
          onFavoriteToggle={handleFavoriteToggle}
        />

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
                <span className="info-value">{album.release_date.toString()}</span>
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

