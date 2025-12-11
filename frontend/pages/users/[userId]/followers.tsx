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
  fetchFollowersAsync,
  fetchMoreFollowersAsync,
  selectCurrentProfile,
  selectFollowers,
  selectLoadingProfile,
  selectLoadingFollowers,
  selectLoadingMoreFollowers,
  selectFollowersPagination,
  selectFollowersHasMore,
  clearCurrentProfile,
  selectCurrentUser,
  selectIsAuthenticated,
  followUserAsync,
  unfollowUserAsync
} from '@/store/slices';

const FollowersPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { userId } = router.query;
  const dispatch = useAppDispatch();

  const loggedUser = useAppSelector(selectCurrentUser);
  const user = useAppSelector(selectCurrentProfile);
  const followers = useAppSelector(selectFollowers);
  const loadingProfile = useAppSelector(selectLoadingProfile);
  const loadingFollowers = useAppSelector(selectLoadingFollowers);
  const loadingMoreFollowers = useAppSelector(selectLoadingMoreFollowers);
  const pagination = useAppSelector(selectFollowersPagination);
  const hasMore = useAppSelector(selectFollowersHasMore);
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  
  const loading = loadingProfile || loadingFollowers;
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
    dispatch(fetchFollowersAsync({ userId: userIdNum, page: 1, size: 20 }));
  }, [userId, dispatch]);

  // Update isFollowing when user data is loaded
  useEffect(() => {
    if (user) {
      setIsFollowing(user.followed ?? false);
    }
  }, [user]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!userId || !hasMore || loadingMoreFollowers) return;
    
    const nextPage = pagination.page + 1;
    await dispatch(fetchMoreFollowersAsync({ 
      userId: parseInt(userId as string), 
      page: nextPage, 
      size: pagination.size 
    }));
  }, [dispatch, userId, pagination.page, pagination.size, hasMore, loadingMoreFollowers]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore,
    isLoading: loading || loadingMoreFollowers,
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
    <Layout title={`Musicboxd - @${user.username} ${t('followers.title')}`}>
      <div className="content-wrapper">
        <UserInfo user={user} isOwnProfile={isOwnProfile} isAuthenticated={isAuthenticated} isFollowing={isFollowing} followLoading={followLoading} onFollowToggle={handleFollowToggle} />

        <h1 className="page-title">{t('followers.title')}</h1>

        {followers.length === 0 && !loadingFollowers ? (
          <p className="no-results">{t('followers.noFollowers')}</p>
        ) : (
          <>
            <div className="users-grid">
              {followers.map((follower) => (
                <UserCard key={follower.id} user={follower} />
              ))}
            </div>

            {/* Sentinel element for infinite scroll */}
            <div ref={sentinelRef} className="infinite-scroll-sentinel" />

            {/* Loading indicator for more content */}
            {(loadingMoreFollowers || isFetchingMore) && (
              <div className="loading-more">
                <LoadingSpinner size="small" />
              </div>
            )}

            {/* End of content message */}
            {!hasMore && followers.length > 0 && (
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

export default FollowersPage;
