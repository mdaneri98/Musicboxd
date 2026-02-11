import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, AlbumInfo } from '@/components/layout';
import { ReviewCard, SongCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  fetchAlbumByIdAsync,
  fetchArtistByIdAsync,
  fetchAlbumSongsAsync,
  fetchAlbumReviewsAsync,
  fetchMoreAlbumReviewsAsync,
  fetchUserAlbumReviewAsync,
  addAlbumFavoriteAsync,
  removeAlbumFavoriteAsync,
  fetchFavoriteAlbumsAsync,
  selectCurrentAlbum,
  selectAlbumSongs,
  selectAlbumReviews,
  selectLoadingAlbum,
  selectLoadingMoreAlbumReviews,
  selectAlbumReviewsPagination,
  selectAlbumReviewsHasMore,
  selectAlbumError,
  selectCurrentUserAlbumReview,
  selectFavoriteAlbums,
  clearCurrentAlbum,
  showError,
} from '@/store/slices';
import type { Artist } from '@/types';

// Constants
const MAX_FAVORITES_ERROR_PATTERN = /maximum number of favorites/i;

const AlbumDetailPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { albumId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  // Use Redux selectors
  const album = useAppSelector(selectCurrentAlbum);
  const songs = useAppSelector(selectAlbumSongs);
  const reviews = useAppSelector(selectAlbumReviews);
  const loading = useAppSelector(selectLoadingAlbum);
  const loadingMoreReviews = useAppSelector(selectLoadingMoreAlbumReviews);
  const reviewsPagination = useAppSelector(selectAlbumReviewsPagination);
  const hasMoreReviews = useAppSelector(selectAlbumReviewsHasMore);
  const error = useAppSelector(selectAlbumError);
  const currentUserReview = useAppSelector(selectCurrentUserAlbumReview);

  const [artist, setArtist] = useState<Artist | null>(null);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const favoriteAlbums = useAppSelector(selectFavoriteAlbums);
  const [isFavorite, setIsFavorite] = useState(false);

  // Clear on unmount
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
        dispatch(fetchArtistByIdAsync(albumData.artist_id))
          .unwrap()
          .then((artistData) => {
            setArtist(artistData);
          })
          .catch((err) => {
            console.error('Failed to fetch artist:', err);
          });
      })
      .catch((err) => {
        console.error('Failed to fetch album:', err);
      });

    // Fetch songs
    dispatch(fetchAlbumSongsAsync({ albumId: albumIdNum, page: 0, size: 100 }));

    // Fetch reviews (initial load)
    dispatch(fetchAlbumReviewsAsync({ albumId: albumIdNum, page: 1, size: 10 }));
  }, [albumId, dispatch]);

  // Fetch current user's review if authenticated and has reviewed
  useEffect(() => {
    if (!albumId || !isAuthenticated || !currentUser || !album?.reviewed) return;

    const albumIdNum = parseInt(albumId as string);
    dispatch(fetchUserAlbumReviewAsync({ albumId: albumIdNum, userId: currentUser.id }));
  }, [albumId, isAuthenticated, currentUser, album?.reviewed, dispatch]);

  // Fetch favorite albums to determine isFavorite status
  useEffect(() => {
    if (!isAuthenticated || !currentUser) return;
    dispatch(fetchFavoriteAlbumsAsync(currentUser.id));
  }, [isAuthenticated, currentUser, dispatch]);

  // Update isFavorite when favorites list or current album changes
  useEffect(() => {
    if (!albumId || !favoriteAlbums) return;
    const albumIdNum = parseInt(albumId as string);
    setIsFavorite(favoriteAlbums.some(a => a.id === albumIdNum));
  }, [albumId, favoriteAlbums]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!albumId || !hasMoreReviews || loadingMoreReviews) return;

    const albumIdNum = parseInt(albumId as string);
    const nextPage = reviewsPagination.page + 1;

    await dispatch(fetchMoreAlbumReviewsAsync({
      albumId: albumIdNum,
      page: nextPage,
      size: reviewsPagination.size
    }));
  }, [dispatch, albumId, reviewsPagination.page, reviewsPagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loading || loadingMoreReviews,
    enabled: !!album && !loading,
  });

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!album) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await dispatch(removeAlbumFavoriteAsync({ userId: currentUser!.id, albumId: album.id })).unwrap();
        setIsFavorite(false);
      } else {
        await dispatch(addAlbumFavoriteAsync({ userId: currentUser!.id, albumId: album.id })).unwrap();
        setIsFavorite(true);
      }
    } catch (err: any) {
      // Check if error is due to max favorites limit
      const errorMessage = err?.message || err || '';
      if (MAX_FAVORITES_ERROR_PATTERN.test(errorMessage)) {
        dispatch(showError(t('errors.favorites.maxReached')));
      } else {
        dispatch(showError(t('errors.favorites.failed')));
      }
    } finally {
      setFavoriteLoading(false);
    }
  };

  if (error) {
    return (
      <Layout title={t('errors.album.title')}>
        <div className="content-wrapper">
          <div className="not-found-container">
            <h1>{t('errors.album.title')}</h1>
            <p>{t('errors.album.message')}</p>
            <button className="btn btn-primary" onClick={() => router.push('/music')}>
              {t('errors.album.backToMusic')}
            </button>
          </div>
        </div>
      </Layout>
    );
  }

  if (loading || !album) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
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
          userRating={currentUserReview?.rating}
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

export default AlbumDetailPage;
