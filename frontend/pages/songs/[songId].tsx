import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, SongInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  fetchSongByIdAsync,
  fetchAlbumByIdAsync,
  fetchArtistByIdAsync,
  fetchSongReviewsAsync,
  fetchMoreSongReviewsAsync,
  fetchUserSongReviewAsync,
  addSongFavoriteAsync,
  removeSongFavoriteAsync,
  fetchFavoriteSongsAsync,
  selectCurrentSong,
  selectSongReviews,
  selectLoadingSong,
  selectLoadingMoreSongReviews,
  selectSongReviewsPagination,
  selectSongReviewsHasMore,
  selectSongError,
  selectCurrentUserSongReview,
  selectFavoriteSongs,
  clearCurrentSong,
  showError,
} from '@/store/slices';
import type { Album, Artist } from '@/types';
import { formatDate } from '@/utils/timeUtils';

// Constants
const MAX_FAVORITES_ERROR_PATTERN = /maximum number of favorites/i;

const SongDetailPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { songId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const song = useAppSelector(selectCurrentSong);
  const reviews = useAppSelector(selectSongReviews);
  const loading = useAppSelector(selectLoadingSong);
  const loadingMoreReviews = useAppSelector(selectLoadingMoreSongReviews);
  const reviewsPagination = useAppSelector(selectSongReviewsPagination);
  const hasMoreReviews = useAppSelector(selectSongReviewsHasMore);
  const error = useAppSelector(selectSongError);
  const currentUserReview = useAppSelector(selectCurrentUserSongReview);

  const [album, setAlbum] = useState<Album | null>(null);
  const [artist, setArtist] = useState<Artist | null>(null);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const favoriteSongs = useAppSelector(selectFavoriteSongs);
  const [isFavorite, setIsFavorite] = useState(false);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentSong());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!songId) return;

    const songIdNum = parseInt(songId as string);
    dispatch(fetchSongByIdAsync(songIdNum))
      .unwrap()
      .then((songData) => {
        dispatch(fetchAlbumByIdAsync(songData.album_id))
          .unwrap()
          .then((albumData) => setAlbum(albumData))
          .catch((err) => console.error(t('common.error'), err));

        dispatch(fetchArtistByIdAsync(songData.artist_id))
          .unwrap()
          .then((artistData) => setArtist(artistData))
          .catch((err) => console.error(t('common.error'), err));
      })
      .catch((err) => console.error(t('common.error'), err));

    // Fetch reviews (initial load)
    dispatch(fetchSongReviewsAsync({ songId: songIdNum, page: 1, size: 10 }));
  }, [songId, dispatch, t]);

  // Fetch current user's review if authenticated and has reviewed
  useEffect(() => {
    if (!songId || !isAuthenticated || !currentUser || !song?.reviewed) return;

    const songIdNum = parseInt(songId as string);
    dispatch(fetchUserSongReviewAsync({ songId: songIdNum, userId: currentUser.id }));
  }, [songId, isAuthenticated, currentUser, song?.reviewed, dispatch]);

  // Fetch favorite songs to determine isFavorite status
  useEffect(() => {
    if (!isAuthenticated || !currentUser) return;
    dispatch(fetchFavoriteSongsAsync(currentUser.id));
  }, [isAuthenticated, currentUser, dispatch]);

  // Update isFavorite when favorites list or current song changes
  useEffect(() => {
    if (!songId || !favoriteSongs) return;
    const songIdNum = parseInt(songId as string);
    setIsFavorite(favoriteSongs.some(s => s.id === songIdNum));
  }, [songId, favoriteSongs]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!songId || !hasMoreReviews || loadingMoreReviews) return;

    const songIdNum = parseInt(songId as string);
    const nextPage = reviewsPagination.page + 1;

    await dispatch(fetchMoreSongReviewsAsync({
      songId: songIdNum,
      page: nextPage,
      size: reviewsPagination.size
    }));
  }, [dispatch, songId, reviewsPagination.page, reviewsPagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loading || loadingMoreReviews,
    enabled: !!song && !loading,
  });

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!song) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await dispatch(removeSongFavoriteAsync({ userId: currentUser!.id, songId: song.id })).unwrap();
        setIsFavorite(false);
      } else {
        await dispatch(addSongFavoriteAsync({ userId: currentUser!.id, songId: song.id })).unwrap();
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

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  if (error) {
    return (
      <Layout title={t('errors.song.title')}>
        <div className="content-wrapper">
          <div className="not-found-container">
            <h1>{t('errors.song.title')}</h1>
            <p>{t('errors.song.message')}</p>
            <button className="btn btn-primary" onClick={() => router.push('/music')}>
              {t('errors.song.backToMusic')}
            </button>
          </div>
        </div>
      </Layout>
    );
  }

  if (loading || !song) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
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
          userRating={currentUserReview?.rating}
          isReviewed={song.reviewed || false}
          onFavoriteToggle={handleFavoriteToggle}
        />

        {/* Song Details */}
        <section className="entity-section song-details">
          <div className="song-info-grid">
            <div className="song-info-item">
              <span className="info-label">{t('song.duration')}:</span>
              <span className="info-value">{formatDuration(parseInt(song.duration))}</span>
            </div>
            {album?.genre && (
              <div className="song-info-item">
                <span className="info-label">{t('song.genre')}:</span>
                <span className="info-value">{album.genre}</span>
              </div>
            )}
            {album?.release_date && (
              <div className="song-info-item">
                <span className="info-label">{t('song.releaseDate')}:</span>
                <span className="info-value">{formatDate(song.release_date)}</span>
              </div>
            )}
          </div>
        </section>

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

export default SongDetailPage;
