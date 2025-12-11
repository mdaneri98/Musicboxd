import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { UserCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  fetchUserByIdAsync,
  fetchFollowersAsync,
  selectCurrentProfile,
  selectFollowers,
  selectLoadingProfile,
  selectLoadingFollowers,
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
  const loading = loadingProfile || loadingFollowers;
  const [isFollowing, setIsFollowing] = useState(user?.followed ?? false);
  const [followLoading, setFollowLoading] = useState(false);
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
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
    dispatch(fetchFollowersAsync({ userId: userIdNum, page, size: 20 }))
      .unwrap()
      .then((followersData) => {
        setHasMore(followersData.items.length === 20);
      })
      .catch((err) => console.error('Failed to fetch followers:', err));
  }, [userId, page, dispatch]);

  // Update isFollowing when user data is loaded
  useEffect(() => {
    if (user) {
      setIsFollowing(user.followed ?? false);
    }
  }, [user]);

  if (loading || !user) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <div className="loading">{t('common.loading')}</div>
        </div>
      </Layout>
    );
  }

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

  return (
    <Layout title={`Musicboxd - @${user.username} ${t('followers.title')}`}>
      <div className="content-wrapper">
        <UserInfo user={user} isOwnProfile={isOwnProfile} isAuthenticated={isAuthenticated} isFollowing={isFollowing} followLoading={followLoading} onFollowToggle={handleFollowToggle} />

        <h1 className="page-title">{t('followers.title')}</h1>

        {followers.length === 0 ? (
          <p className="no-results">{t('followers.noFollowers')}</p>
        ) : (
          <>
            <div className="users-grid">
              {followers.map((follower) => (
                <UserCard key={follower.id} user={follower} />
              ))}
            </div>

            <div className="pagination">
              {page > 1 && (
                <button
                  onClick={() => setPage(page - 1)}
                  className="btn btn-secondary"
                >
                  {t('followers.previousPage')}
                </button>
              )}
              {hasMore && (
                <button
                  onClick={() => setPage(page + 1)}
                  className="btn btn-secondary"
                >
                  {t('followers.nextPage')}
                </button>
              )}
            </div>
          </>
        )}
      </div>
    </Layout>
  );
};

export default FollowersPage;
