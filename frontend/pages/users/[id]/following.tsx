import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
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
  followUserAsync
} from '@/store/slices';

const FollowingPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const dispatch = useAppDispatch();
  
  const loggedUser = useAppSelector(selectCurrentUser);
  const user = useAppSelector(selectCurrentProfile);
  const following = useAppSelector(selectFollowing);
  const loadingProfile = useAppSelector(selectLoadingProfile);
  const loadingFollowing = useAppSelector(selectLoadingFollowing);
  const [isOwnProfile, setIsOwnProfile] = useState(loggedUser?.id === parseInt(id as string));
  const [isFollowing, setIsFollowing] = useState(user?.is_followed_by_logged_user ?? false);
  const [followLoading, setFollowLoading] = useState(false);
  const loading = loadingProfile || loadingFollowing;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentProfile());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!id) return;
    
    const userId = parseInt(id as string);
    dispatch(fetchUserByIdAsync(userId));
    setIsFollowing(user?.is_followed_by_logged_user ?? false);
    
    dispatch(fetchFollowingAsync({ userId, page, size: 20 }))
      .unwrap()
      .then((followingData) => {
        setHasMore(followingData.items.length === 20);
      })
      .catch((err) => console.error('Failed to fetch following:', err));
  }, [id, page, dispatch]);

  if (loading || !user) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading...</div>
        </div>
      </Layout>
    );
  }

  const handleFollowToggle = async () => {
    setFollowLoading(true);
    await dispatch(followUserAsync(user.id));
    setIsFollowing(true);
    setFollowLoading(false);
  };

  return (
    <Layout title={`Musicboxd - @${user.username} Following`}>
      <div className="content-wrapper">
        <div className="profile-header">
          <header>
            <UserInfo user={user} isOwnProfile={isOwnProfile} isAuthenticated={isAuthenticated} isFollowing={isFollowing} followLoading={followLoading} onFollowToggle={handleFollowToggle} />
          </header>
        </div>

        <h1 className="page-title">Following</h1>

        {following.length === 0 ? (
          <p className="no-results">Not following anyone yet</p>
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
                  Previous Page
                </button>
              )}
              {hasMore && (
                <button
                  onClick={() => setPage(page + 1)}
                  className="btn btn-secondary"
                >
                  Next Page
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
