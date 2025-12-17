import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { UserCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import {
  fetchUserByIdAsync,
  fetchFollowingAsync,
  fetchMoreFollowingAsync,
  selectCurrentProfile,
  selectFollowing,
  selectLoadingProfile,
  selectLoadingFollowing,
  selectLoadingMoreFollowing,
  selectFollowingPagination,
  selectFollowingHasMore,
  clearCurrentProfile,
  selectCurrentUser,
  selectIsAuthenticated,
  followUserAsync,
  unfollowUserAsync
} from '@/store/slices';

const FollowingPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { userId } = router.query;
  const dispatch = useAppDispatch();

  const loggedUser = useAppSelector(selectCurrentUser);
  const user = useAppSelector(selectCurrentProfile);
  const following = useAppSelector(selectFollowing);
  const loadingProfile = useAppSelector(selectLoadingProfile);
  const loadingFollowing = useAppSelector(selectLoadingFollowing);
  const loadingMoreFollowing = useAppSelector(selectLoadingMoreFollowing);
  const pagination = useAppSelector(selectFollowingPagination);
  const hasMore = useAppSelector(selectFollowingHasMore);
  const isAuthenticated = useAppSelector(selectIsAuthenticated);

  const loading = loadingProfile || loadingFollowing;
  const [isFollowing, setIsFollowing] = useState(false);
  const [followLoading, setFollowLoading] = useState(false);
  const isOwnProfile = loggedUser?.id === parseInt(userId as string);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentProfile());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!userId) return;

    const userIdNum = parseInt(userId as string);
    dispatch(fetchUserByIdAsync(userIdNum));
    dispatch(fetchFollowingAsync({ userId: userIdNum, page: 1, size: 10 }));
  }, [userId, dispatch]);

  // Update isFollowing when user data is loaded
  useEffect(() => {
    if (user) {
      setIsFollowing(user.followed ?? false);
    }
  }, [user]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!userId || !hasMore || loadingMoreFollowing) return;

    const nextPage = pagination.page + 1;
    await dispatch(fetchMoreFollowingAsync({
      userId: parseInt(userId as string),
      page: nextPage,
      size: pagination.size
    }));
  }, [dispatch, userId, pagination.page, pagination.size, hasMore, loadingMoreFollowing]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore,
    isLoading: loading || loadingMoreFollowing,
    enabled: !!user && !loading,
  });

  const handleFollowToggle = async () => {
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

  if (loading || !user) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - @${user.username} ${t('following.title')}`}>
      <div className="content-wrapper">
        <UserInfo user={user} isOwnProfile={isOwnProfile} isAuthenticated={isAuthenticated} isFollowing={isFollowing} followLoading={followLoading} onFollowToggle={handleFollowToggle} />

        <h1 className="page-title">{t('following.title')}</h1>

        {following.length === 0 && !loadingFollowing ? (
          <p className="no-results">{t('following.notFollowing')}</p>
        ) : (
          <>
            <div className="users-grid">
              {following.map((followedUser) => (
                <UserCard key={followedUser.id} user={followedUser} />
              ))}
            </div>

            {/* Sentinel element for infinite scroll */}
            <div ref={sentinelRef} className="infinite-scroll-sentinel" />

            {/* Loading indicator for more content */}
            {(loadingMoreFollowing || isFetchingMore) && (
              <div className="loading-more">
                <LoadingSpinner size="small" />
              </div>
            )}

            {/* End of content message */}
            {!hasMore && following.length > 0 && (
              <div className="end-of-content">
                <p>{t('common.noMoreContent')}</p>
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
};

export default FollowingPage;
