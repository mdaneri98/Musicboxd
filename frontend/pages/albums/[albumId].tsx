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
  const { albumId } = router.query;
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
    if (!albumId) return;
    
    const albumIdNum = parseInt(albumId as string);
    dispatch(fetchAlbumByIdAsync(albumIdNum))
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
<<<<<<< HEAD:frontend/pages/albums/[id].tsx
    dispatch(fetchAlbumSongsAsync({ albumId, page: 1, size: 100 }));
  }, [id, dispatch]);
=======
    dispatch(fetchAlbumSongsAsync({ albumId: albumIdNum, page: 0, size: 100 }));
  }, [albumId, dispatch]);
>>>>>>> 1eec8ecc86241f8a906cfd3d0cd3ebbe658a87bd:frontend/pages/albums/[albumId].tsx

  // Fetch reviews
  useEffect(() => {
    if (!albumId) return;
    
    const albumIdNum = parseInt(albumId as string);
    dispatch(fetchAlbumReviewsAsync({ albumId: albumIdNum, page: reviewsPage, size: 20 }))
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
  }, [albumId, reviewsPage, isAuthenticated, currentUser, dispatch]);

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
<<<<<<< HEAD:frontend/pages/albums/[id].tsx
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
=======
        {/* Album Header */}
        <section className="entity-header">
          <div className="entity-main-info">
            <img src={albumImgUrl} alt={album.title} className="entity-image album-cover" />
            <div className="entity-details">
              <div className="entity-type">
                Album
                {currentUser && currentUser.moderator && (
                  <Link href={`/mod/albums/${album.id}/edit`} className="edit-link">
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
>>>>>>> 1eec8ecc86241f8a906cfd3d0cd3ebbe658a87bd:frontend/pages/albums/[albumId].tsx

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

