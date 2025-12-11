import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { UserCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import {
  fetchUserByIdAsync,
  fetchFollowingAsync,
  selectCurrentProfile,
  selectFollowing,
  selectLoadingProfile,
  selectLoadingFollowing,
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
  const [isFollowing, setIsFollowing] = useState(user?.followed ?? false);
  const [followLoading, setFollowLoading] = useState(false);
  const loading = loadingProfile || loadingFollowing;
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
    
    dispatch(fetchFollowingAsync({ userId: userIdNum, page, size: 20 }))
      .unwrap()
      .then((followingData) => {
        setHasMore(followingData.items.length === 20);
      })
      .catch((err) => console.error('Failed to fetch following:', err));
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
    <Layout title={`Musicboxd - @${user.username} ${t('following.title')}`}>
      <div className="content-wrapper">
        <UserInfo user={user} isOwnProfile={isOwnProfile} isAuthenticated={isAuthenticated} isFollowing={isFollowing} followLoading={followLoading} onFollowToggle={handleFollowToggle} />

        <h1 className="page-title">{t('following.title')}</h1>

        {following.length === 0 ? (
          <p className="no-results">{t('following.notFollowing')}</p>
        ) : (
          <>
            <div className="users-grid">
              {following.map((followedUser) => (
                <UserCard key={followedUser.id} user={followedUser} />
              ))}
            </div>

            <div className="pagination">
              {page > 1 && (
                <button
                  onClick={() => setPage(page - 1)}
                  className="btn btn-secondary"
                >
                  {t('following.previousPage')}
                </button>
              )}
              {hasMore && (
                <button
                  onClick={() => setPage(page + 1)}
                  className="btn btn-secondary"
                >
                  {t('following.nextPage')}
                </button>
              )}
            </div>
          </>
        )}
      </div>
    </Layout>
  );
};

export default FollowingPage;
