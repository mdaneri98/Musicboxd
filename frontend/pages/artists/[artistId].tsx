import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, ArtistInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  fetchArtistByIdAsync, 
  fetchArtistAlbumsAsync, 
  fetchArtistSongsAsync, 
  fetchArtistReviewsAsync, 
  fetchMoreArtistReviewsAsync,
  addArtistFavoriteAsync, 
  removeArtistFavoriteAsync,
  selectCurrentArtist,
  selectArtistAlbums,
  selectArtistSongs,
  selectArtistReviews,
  selectLoadingArtist,
  selectLoadingMoreArtistReviews,
  selectArtistReviewsPagination,
  selectArtistReviewsHasMore,
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
  
  // Use Redux selectors
  const artist = useAppSelector(selectCurrentArtist);
  const albums = useAppSelector(selectArtistAlbums);
  const songs = useAppSelector(selectArtistSongs);
  const reviews = useAppSelector(selectArtistReviews);
  const loading = useAppSelector(selectLoadingArtist);
  const loadingMoreReviews = useAppSelector(selectLoadingMoreArtistReviews);
  const reviewsPagination = useAppSelector(selectArtistReviewsPagination);
  const hasMoreReviews = useAppSelector(selectArtistReviewsHasMore);
  const error = useAppSelector(selectArtistError);
  
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
    dispatch(fetchArtistReviewsAsync({ artistId: artistIdNum, page: 1, size: 20 }));
  }, [artistId, dispatch]);

  // Set user rating when reviews and user data are available
  useEffect(() => {
    if (artist?.reviewed && isAuthenticated && currentUser && reviews.length > 0) {
      const userReview = reviews.find((r: Review) => r.user_id === currentUser.id);
      if (userReview) {
        setUserRating(userReview.rating);
      }
    }
  }, [artist?.reviewed, isAuthenticated, currentUser, reviews]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!artistId || !hasMoreReviews || loadingMoreReviews) return;
    
    const artistIdNum = parseInt(artistId as string);
    const nextPage = reviewsPagination.page + 1;
    
    await dispatch(fetchMoreArtistReviewsAsync({ 
      artistId: artistIdNum, 
      page: nextPage, 
      size: reviewsPagination.size 
    }));
  }, [dispatch, artistId, reviewsPagination.page, reviewsPagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loading || loadingMoreReviews,
    enabled: !!artist && !loading,
  });

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
          <LoadingSpinner size="large" message={t('artist.loadingArtist')} />
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

            {/* Sentinel element for infinite scroll */}
            <div ref={sentinelRef} className="infinite-scroll-sentinel" />

            {/* Loading indicator for more content */}
            {(loadingMoreReviews || isFetchingMore) && (
              <div className="loading-more">
                <LoadingSpinner size="small" />
              </div>
            )}

            {/* End of content message */}
            {!hasMoreReviews && reviews.length > 0 && (
              <div className="end-of-content">
                <p>{t('common.noMoreContent')}</p>
              </div>
            )}
          </section>
        )}
      </div>
    </Layout>
  );
};

export default ArtistDetailPage;
