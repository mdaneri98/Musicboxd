import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, ArtistInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  fetchArtistByIdAsync, 
  fetchArtistAlbumsAsync, 
  fetchArtistSongsAsync, 
  fetchArtistReviewsAsync, 
  addArtistFavoriteAsync, 
  removeArtistFavoriteAsync,
  selectCurrentArtist,
  selectArtistAlbums,
  selectArtistSongs,
  selectArtistReviews,
  selectLoadingArtist,
  selectArtistError,
  clearCurrentArtist
} from '@/store/slices';
import type { Review } from '@/types';
import { AlbumCard, SongCard } from '@/components/cards';

const ArtistDetailPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { artistId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  // Use Redux selectors instead of useState
  const artist = useAppSelector(selectCurrentArtist);
  const albums = useAppSelector(selectArtistAlbums);
  const songs = useAppSelector(selectArtistSongs);
  const reviews = useAppSelector(selectArtistReviews);
  const loading = useAppSelector(selectLoadingArtist);
  const error = useAppSelector(selectArtistError);
  
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();


  // Clear artist data when component unmounts or id changes
  useEffect(() => {
    return () => {
      dispatch(clearCurrentArtist());
    };
  }, [dispatch]);

  // Fetch artist data
  useEffect(() => {
    if (!artistId) return;
    
    const artistIdNum = parseInt(artistId as string);
    dispatch(fetchArtistByIdAsync(artistIdNum));
    dispatch(fetchArtistAlbumsAsync({ artistId: artistIdNum, page: 1, size: 10 }));
    dispatch(fetchArtistSongsAsync({ artistId: artistIdNum, page: 1, size: 10 }));
    if(artist?.reviewed && isAuthenticated && currentUser) {
      const userReview = reviews.find((r: Review) => r.user_id === currentUser.id);
      if (userReview) {
        setUserRating(userReview.rating);
      }
    }
  }, [artistId, dispatch]);

  // Fetch reviews with pagination
  useEffect(() => {
    if (!artistId) return;
    
    const artistIdNum = parseInt(artistId as string);
    dispatch(fetchArtistReviewsAsync({ artistId: artistIdNum, page: reviewsPage, size: 20 }))
      .unwrap()
      .then((reviewsData) => {
        setHasMoreReviews(reviewsData.items.length === 20);
      })
      .catch((err) => {
        console.error('Failed to fetch reviews:', err);
      });
  }, [artistId, reviewsPage, isAuthenticated, currentUser, dispatch]);

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!artist) return;

    try {
      setFavoriteLoading(true);
      if (artist.favorite) {
        await dispatch(removeArtistFavoriteAsync(artist.id)).unwrap();
      } else {
        await dispatch(addArtistFavoriteAsync(artist.id)).unwrap();
      }
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  if (loading || !artist) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <div className="loading">{t('artist.loadingArtist')}</div>
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
    <Layout title={`Musicboxd - ${artist.name}`}>
      <div className="content-wrapper">
        <ArtistInfo
          artist={artist}
          currentUser={currentUser}
          isAuthenticated={isAuthenticated}
          isFavorite={artist.favorite || false}
          favoriteLoading={favoriteLoading}
          userRating={userRating}
          isReviewed={artist.reviewed || false}
          onFavoriteToggle={handleFavoriteToggle}
        />

        {/* Albums Section */}
        {albums.length > 0 && (
          <section className="entity-section">
            <h2>{t('music.albums')}</h2>
            <div className="carousel-container">
              <div className="carousel">
                {albums.map((album) => (
                  <AlbumCard key={album.id} album={album} />
                ))}
              </div>
            </div>
          </section>
        )}

        {/* Songs Section */}
        {songs.length > 0 && (
          <section className="entity-section">
            <h2>{t('artist.popularSongs')}</h2>
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
                  {t('artist.previousPage')}
                </button>
              )}
              {hasMoreReviews && (
                <button
                  onClick={() => setReviewsPage(reviewsPage + 1)}
                  className="btn btn-secondary"
                >
                  {t('artist.nextPage')}
                </button>
              )}
            </div>
          </section>
        )}
      </div>
    </Layout>
  );
};

export default ArtistDetailPage;
