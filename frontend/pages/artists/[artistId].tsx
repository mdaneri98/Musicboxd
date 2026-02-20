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
  fetchUserArtistReviewAsync,
  addArtistFavoriteAsync,
  removeArtistFavoriteAsync,
  fetchFavoriteArtistsAsync,
  selectCurrentArtist,
  selectArtistAlbums,
  selectArtistSongs,
  selectArtistReviews,
  selectLoadingArtist,
  selectLoadingMoreArtistReviews,
  selectArtistReviewsPagination,
  selectArtistReviewsHasMore,
  selectArtistError,
  selectCurrentUserArtistReview,
  selectFavoriteArtists,
  clearCurrentArtist,
  showError,
  fetchReviewLikedStatusAsync,
} from '@/store/slices';
import { AlbumCard, SongCard } from '@/components/cards';

// Constants
const MAX_FAVORITES_ERROR_PATTERN = /maximum number of favorites/i;

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
  const currentUserReview = useAppSelector(selectCurrentUserArtistReview);

  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const favoriteArtists = useAppSelector(selectFavoriteArtists);
  const [isFavorite, setIsFavorite] = useState(false);

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
    dispatch(fetchArtistReviewsAsync({ artistId: artistIdNum, page: 1, size: 10 }));
  }, [artistId, dispatch]);

  // Fetch current user's review if authenticated
  useEffect(() => {
    if (!artistId || !isAuthenticated || !currentUser) return;

    const artistIdNum = parseInt(artistId as string);
    dispatch(fetchUserArtistReviewAsync({ artistId: artistIdNum, userId: currentUser.id }));
  }, [artistId, isAuthenticated, currentUser, dispatch]);

  // Fetch favorite artists to determine isFavorite status
  useEffect(() => {
    if (!isAuthenticated || !currentUser) return;
    dispatch(fetchFavoriteArtistsAsync(currentUser.id));
  }, [isAuthenticated, currentUser, dispatch]);

  // Update isFavorite when favorites list or current artist changes
  useEffect(() => {
    if (!artistId || !favoriteArtists) return;
    const artistIdNum = parseInt(artistId as string);
    setIsFavorite(favoriteArtists.some(a => a.id === artistIdNum));
  }, [artistId, favoriteArtists]);

  // Batch fetch liked status for reviews
  useEffect(() => {
    if (!isAuthenticated || !currentUser || reviews.length === 0) return;

    const idsWithoutLikedStatus = reviews
      .filter((r) => r.liked === undefined)
      .map((r) => r.id);

    if (idsWithoutLikedStatus.length > 0) {
      dispatch(fetchReviewLikedStatusAsync({ reviewIds: idsWithoutLikedStatus, userId: currentUser.id }));
    }
  }, [isAuthenticated, currentUser, reviews, dispatch]);

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
      if (isFavorite) {
        await dispatch(removeArtistFavoriteAsync({ userId: currentUser!.id, artistId: artist.id })).unwrap();
        setIsFavorite(false);
      } else {
        await dispatch(addArtistFavoriteAsync({ userId: currentUser!.id, artistId: artist.id })).unwrap();
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
      <Layout title={t('errors.artist.title')}>
        <div className="content-wrapper">
          <div className="not-found-container">
            <h1>{t('errors.artist.title')}</h1>
            <p>{t('errors.artist.message')}</p>
            <button className="btn btn-primary" onClick={() => router.push('/music')}>
              {t('errors.artist.backToMusic')}
            </button>
          </div>
        </div>
      </Layout>
    );
  }

  if (loading || !artist) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
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
          isFavorite={isFavorite}
          favoriteLoading={favoriteLoading}
          userRating={currentUserReview?.rating}
          isReviewed={!!currentUserReview}
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
