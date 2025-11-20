import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout, AlbumInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser,
  fetchAlbumByIdAsync,
  fetchArtistByIdAsync,
  fetchAlbumSongsAsync,
  fetchAlbumReviewsAsync,
  addAlbumFavoriteAsync,
  removeAlbumFavoriteAsync,
  selectCurrentAlbum,
  selectAlbumSongs,
  selectAlbumReviews,
  selectLoadingAlbum,
  selectAlbumError,
  clearCurrentAlbum
} from '@/store/slices';
import type { Review, HALResource, Artist } from '@/types';

const AlbumDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  // Usar selectores de Redux
  const album = useAppSelector(selectCurrentAlbum);
  const songs = useAppSelector(selectAlbumSongs);
  const reviews = useAppSelector(selectAlbumReviews);
  const loading = useAppSelector(selectLoadingAlbum);
  const error = useAppSelector(selectAlbumError);
  
  const [artist, setArtist] = useState<Artist | null>(null);
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();
  const [isReviewed, setIsReviewed] = useState(false);
  
  const isFavorite = album?.is_favorite || false;

  // Limpiar al desmontar
  useEffect(() => {
    return () => {
      dispatch(clearCurrentAlbum());
    };
  }, [dispatch]);

  // Fetch album data
  useEffect(() => {
    if (!id) return;
    
    const albumId = parseInt(id as string);
    dispatch(fetchAlbumByIdAsync(albumId))
      .unwrap()
      .then((albumData) => {
        // Fetch artist data
        dispatch(fetchArtistByIdAsync(albumData.data.artist_id))
          .unwrap()
          .then((artistData) => {
            setArtist(artistData.data);
          })
          .catch((err) => {
            console.error('Failed to fetch artist:', err);
          });
      })
      .catch((err) => {
        console.error('Failed to fetch album:', err);
      });
    
    // Fetch songs
    dispatch(fetchAlbumSongsAsync({ albumId, page: 1, size: 100 }));
  }, [id, dispatch]);

  // Fetch reviews
  useEffect(() => {
    if (!id) return;
    
    const albumId = parseInt(id as string);
    dispatch(fetchAlbumReviewsAsync({ albumId, page: reviewsPage, size: 20 }))
      .unwrap()
      .then((reviewsData) => {
        setHasMoreReviews(reviewsData.items.length === 20);
        
        // Check if current user has reviewed this album
        if (isAuthenticated && currentUser) {
          const userReview = reviewsData.items.find((r: HALResource<Review>) => r.data.user_id === currentUser.id);
          if (userReview) {
            setIsReviewed(true);
            setUserRating(userReview.data.rating || 0);
          }
        }
      })
      .catch((err) => {
        console.error('Failed to fetch reviews:', err);
      });
  }, [id, reviewsPage, isAuthenticated, currentUser, dispatch]);

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!album) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await dispatch(removeAlbumFavoriteAsync(album.id)).unwrap();
      } else {
        await dispatch(addAlbumFavoriteAsync(album.id)).unwrap();
      }
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  if (loading || !album) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading album...</div>
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
    <Layout title={`Musicboxd - ${album.title}`}>
      <div className="content-wrapper">
        <AlbumInfo
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

        {/* Songs Section */}
        {songs.length > 0 && (
          <section className="entity-section">
            <h2>Songs</h2>
            <ul className="song-list">
              {songs.map((song, index) => (
                <li key={song.id}>
                  <Link href={`/songs/${song.id}`} className="song-item">
                    <span className="song-number">{index + 1}</span>
                    <span className="song-title">{song.title}</span>
                    {song.avg_rating && (
                      <div className="rating-badge">
                        <span className="rating">{song.avg_rating.toFixed(1)}</span>
                        <span className="star">&#9733;</span>
                      </div>
                    )}
                  </Link>
                </li>
              ))}
            </ul>
          </section>
        )}

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

export default AlbumDetailPage;

