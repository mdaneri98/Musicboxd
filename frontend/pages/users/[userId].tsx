import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { LoadingSpinner } from '@/components/ui';
import { FavoritesSection, ReviewsSection } from '@/components/profile';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import { 
  selectIsAuthenticated, 
  fetchUserByIdAsync,
  fetchFavoriteArtistsAsync,
  fetchFavoriteAlbumsAsync,
  fetchFavoriteSongsAsync,
  fetchUserReviewsAsync,
  fetchMoreUserReviewsAsync,
  followUserAsync,
  unfollowUserAsync,
  selectCurrentUser,
  selectUserById,
  selectFavoriteArtists,
  selectFavoriteAlbums,
  selectFavoriteSongs,
  selectUserReviews,
  selectUserReviewsPagination,
  selectUserReviewsHasMore,
  clearCurrentProfile,
  selectLoadingReviews,
  selectLoadingMoreReviews,
  selectLoadingFavorites,
  selectUserError,
  selectLoadingProfile
} from '@/store/slices';
import { ProfileTabEnum } from '@/types';

const UserProfilePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { userId, tab: queryTab } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const loggedUser = useAppSelector(selectCurrentUser);
  const user = useAppSelector(selectUserById(parseInt(userId as string)));
  
  const [activeTab, setActiveTab] = useState<ProfileTabEnum>(queryTab ? queryTab as ProfileTabEnum : ProfileTabEnum.FAVORITES);
  const favoriteArtists = useAppSelector(selectFavoriteArtists);
  const favoriteAlbums = useAppSelector(selectFavoriteAlbums);
  const favoriteSongs = useAppSelector(selectFavoriteSongs);
  const reviews = useAppSelector(selectUserReviews);
  const loadingFavorites = useAppSelector(selectLoadingFavorites);
  const loadingReviews = useAppSelector(selectLoadingReviews);
  const loadingMoreReviews = useAppSelector(selectLoadingMoreReviews);
  const reviewsPagination = useAppSelector(selectUserReviewsPagination);
  const hasMoreReviews = useAppSelector(selectUserReviewsHasMore);
  const error = useAppSelector(selectUserError);
  const loadingProfile = useAppSelector(selectLoadingProfile);
  const [isFollowing, setIsFollowing] = useState(false);
  const [followLoading, setFollowLoading] = useState(false);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentProfile());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!userId) return;

    const userIdNum = parseInt(userId as string);

    if (loggedUser && loggedUser.id === userIdNum) {
      router.push(`/profile?tab=${activeTab}`);
      return;
    }
    
    dispatch(fetchUserByIdAsync(userIdNum));
    dispatch(fetchFavoriteArtistsAsync(userIdNum));
    dispatch(fetchFavoriteAlbumsAsync(userIdNum));
    dispatch(fetchFavoriteSongsAsync(userIdNum));
    dispatch(fetchUserReviewsAsync({ userId: userIdNum, page: 1, size: 10 }));
  }, [userId, dispatch, loggedUser, router, activeTab]);

  // Update isFollowing when user data is loaded
  useEffect(() => {
    if (user) {
      setIsFollowing(user.followed ?? false);
    }
  }, [user]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!userId || !hasMoreReviews || loadingMoreReviews) return;
    
    const userIdNum = parseInt(userId as string);
    const nextPage = reviewsPagination.page + 1;
    
    await dispatch(fetchMoreUserReviewsAsync({ 
      userId: userIdNum, 
      page: nextPage, 
      size: reviewsPagination.size 
    }));
  }, [dispatch, userId, reviewsPagination.page, reviewsPagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loadingReviews || loadingMoreReviews,
    enabled: activeTab === ProfileTabEnum.REVIEWS && !!user && !loadingReviews,
  });

  const handleFollowToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!user) return;

    try {
      setFollowLoading(true);
      if (isFollowing) {
        await dispatch(unfollowUserAsync(user.id)).unwrap();
        setIsFollowing(false);
      } else {
        await dispatch(followUserAsync(user.id)).unwrap();
        setIsFollowing(true);
      }
    } catch (error) {
      console.error('Failed to toggle follow:', error);
    } finally {
      setFollowLoading(false);
    }
  };

  const handleTabChange = (tab: ProfileTabEnum) => {
    setActiveTab(tab);
  };

  if (error) {
    return (
      <Layout title={t('errors.user.title')}>
        <div className="content-wrapper">
          <div className="not-found-container">
            <h1>{t('errors.user.title')}</h1>
            <p>{t('errors.user.message')}</p>
            <button className="btn btn-primary" onClick={() => router.push('/')}>
              {t('errors.user.backToHome')}
            </button>
          </div>
        </div>
      </Layout>
    );
  }

  if (loadingProfile || loadingFavorites || !user) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - ${user.username}'s ${t('userProfile.profile')}`}>
      <div className="content-wrapper">
        <UserInfo
          user={user}
          isOwnProfile={false}
          isAuthenticated={isAuthenticated}
          isFollowing={isFollowing}
          followLoading={followLoading}
          onFollowToggle={handleFollowToggle}
        />

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
            favoriteArtists={favoriteArtists}
            favoriteAlbums={favoriteAlbums}
            favoriteSongs={favoriteSongs}
            loading={loadingFavorites}
            isOwnProfile={false}
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

export default UserProfilePage;
