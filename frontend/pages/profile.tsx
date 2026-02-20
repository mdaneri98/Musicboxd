import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { FavoritesSection, ReviewsSection } from '@/components/profile';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  getCurrentUserAsync,
  selectUserReviewsPagination,
  fetchFavoriteArtistsAsync,
  fetchFavoriteAlbumsAsync,
  fetchFavoriteSongsAsync,
  fetchUserReviewsAsync,
  fetchMoreUserReviewsAsync,
  selectFavoriteArtists,
  selectFavoriteAlbums,
  selectFavoriteSongs,
  selectUserReviews,
  selectLoadingFavorites,
  selectLoadingReviews,
  selectLoadingMoreReviews,
  selectUserReviewsHasMore,
  fetchReviewLikedStatusAsync,
} from '@/store/slices';
import { ProfileTabEnum } from '@/types';

const ProfilePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { tab: queryTab } = router.query;

  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [activeTab, setActiveTab] = useState<ProfileTabEnum>(queryTab ? queryTab as ProfileTabEnum : ProfileTabEnum.FAVORITES);
  const favoriteArtists = useAppSelector(selectFavoriteArtists);
  const favoriteAlbums = useAppSelector(selectFavoriteAlbums);
  const favoriteSongs = useAppSelector(selectFavoriteSongs);
  const reviews = useAppSelector(selectUserReviews);
  const loadingFavorites = useAppSelector(selectLoadingFavorites);
  const loadingReviews = useAppSelector(selectLoadingReviews);
  const loadingMoreReviews = useAppSelector(selectLoadingMoreReviews);
  const pagination = useAppSelector(selectUserReviewsPagination);
  const hasMoreReviews = useAppSelector(selectUserReviewsHasMore);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!currentUser) {
      dispatch(getCurrentUserAsync());
    }
  }, [isAuthenticated, currentUser, dispatch, router]);

  useEffect(() => {
    if (currentUser) {
      dispatch(fetchFavoriteArtistsAsync(currentUser.id));
      dispatch(fetchFavoriteAlbumsAsync(currentUser.id));
      dispatch(fetchFavoriteSongsAsync(currentUser.id));
      dispatch(fetchUserReviewsAsync({ userId: currentUser.id, page: 1, size: 10 }));
    }
  }, [currentUser, dispatch]);

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
    if (!currentUser || !hasMoreReviews || loadingMoreReviews) return;

    const nextPage = pagination.page + 1;
    await dispatch(fetchMoreUserReviewsAsync({
      userId: currentUser.id,
      page: nextPage,
      size: pagination.size
    }));
  }, [dispatch, currentUser, pagination.page, pagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loadingReviews || loadingMoreReviews,
    enabled: activeTab === ProfileTabEnum.REVIEWS && !!currentUser && !loadingReviews,
  });

  const handleTabChange = (tab: ProfileTabEnum) => {
    setActiveTab(tab);
  };

  if (!isAuthenticated || !currentUser) {
    return null;
  }

  return (
    <Layout title="Musicboxd - My Profile">
      <div className="content-wrapper">
        {/* User Info Header */}
        <UserInfo user={currentUser} isOwnProfile={true} isAuthenticated={isAuthenticated} isFollowing={false} followLoading={false} onFollowToggle={() => { }} />

        {/* Tabs */}
        <div className="tabs">
          <span
            id="favoritesButton"
            className={`tab ${activeTab === ProfileTabEnum.FAVORITES ? 'active' : ''}`}
            onClick={() => handleTabChange(ProfileTabEnum.FAVORITES)}
            style={{ cursor: 'pointer' }}
          >
            {t('profile.tabs.favorites')}
          </span>
          <span
            id="reviewsButton"
            className={`tab ${activeTab === ProfileTabEnum.REVIEWS ? 'active' : ''}`}
            onClick={() => handleTabChange(ProfileTabEnum.REVIEWS)}
            style={{ cursor: 'pointer' }}
          >
            {t('profile.tabs.reviews')}
          </span>
        </div>

        {/* Favorites Section - Lazy loaded */}
        {activeTab === ProfileTabEnum.FAVORITES && (
          <FavoritesSection
            favoriteArtists={Object.values(favoriteArtists)}
            favoriteAlbums={Object.values(favoriteAlbums)}
            favoriteSongs={Object.values(favoriteSongs)}
            loading={loadingFavorites}
            isOwnProfile={true}
          />
        )}

        {/* Reviews Section - Lazy loaded */}
        {activeTab === ProfileTabEnum.REVIEWS && (
          <ReviewsSection
            reviews={reviews}
            loading={loadingReviews}
            loadingMore={loadingMoreReviews}
            isFetchingMore={isFetchingMore}
            hasMore={hasMoreReviews}
            sentinelRef={sentinelRef}
          />
        )}
      </div>
    </Layout>
  );
};

export default ProfilePage;
