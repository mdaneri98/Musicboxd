import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
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
import type { Artist, Review } from '@/types';
import { SongCard } from '@/components/cards';

const AlbumDetailPage = () => {
  const { t } = useTranslation();
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
          if(albumData.data.reviewed && isAuthenticated && currentUser) {
            const userReview = reviews.find((r: Review) => r.user_id === currentUser.id);
            if (userReview) {
              setUserRating(userReview.rating);
            }
          }
      })
      .catch((err) => {
        console.error('Failed to fetch album:', err);
      });
    
    // Fetch songs
    dispatch(fetchAlbumSongsAsync({ albumId: albumIdNum, page: 0, size: 100 }));
  }, [albumId, dispatch]);

  // Fetch reviews
  useEffect(() => {
    if (!albumId) return;
    
    const albumIdNum = parseInt(albumId as string);
    dispatch(fetchAlbumReviewsAsync({ albumId: albumIdNum, page: reviewsPage, size: 20 }))
      .unwrap()
      .then((reviewsData) => {
        setHasMoreReviews(reviewsData.items.length === 20);
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
      if (album.favorite) {
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
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <div className="loading">{t('album.loadingAlbum')}</div>
        </div>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout title={t('common.error')}>
        <div className="content-wrapper">
          <div className="alert alert-danger" role="alert">
            <strong>{t('common.error')}:</strong> {error}
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
          isFavorite={album.favorite || false}
          favoriteLoading={favoriteLoading}
          userRating={userRating}
          isReviewed={album.reviewed || false}
          onFavoriteToggle={handleFavoriteToggle}
        />

        {/* Songs Section */}
        {songs.length > 0 && (
          <section className="entity-section">
            <h2>{t('music.songs')}</h2>
            <ul className="song-list">
              {songs.map((song, index) => (
                <SongCard key={song.id} song={song} index={index} />
              ))}
            </ul>
          </section>
        )}

        {/* Reviews Section */}
        {reviews.length > 0 && (
          <section className="entity-section">
            <h2>{t('profile.reviews')}</h2>
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
                  {t('album.previousPage')}
                </button>
              )}
              {hasMoreReviews && (
                <button
                  onClick={() => setReviewsPage(reviewsPage + 1)}
                  className="btn btn-secondary"
                >
                  {t('album.nextPage')}
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

