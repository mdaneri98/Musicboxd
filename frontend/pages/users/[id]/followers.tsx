import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
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
  clearCurrentProfile
} from '@/store/slices';

const FollowersPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const dispatch = useAppDispatch();
  
  const user = useAppSelector(selectCurrentProfile);
  const followers = useAppSelector(selectFollowers);
  const loadingProfile = useAppSelector(selectLoadingProfile);
  const loadingFollowers = useAppSelector(selectLoadingFollowers);
  const loading = loadingProfile || loadingFollowers;
  
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
    
    dispatch(fetchFollowersAsync({ userId, page, size: 20 }))
      .unwrap()
      .then((followersData) => {
        setHasMore(followersData.items.length === 20);
      })
      .catch((err) => console.error('Failed to fetch followers:', err));
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

  return (
    <Layout title={`Musicboxd - @${user.username} Followers`}>
      <div className="content-wrapper">
        <div className="profile-header">
          <header>
            <UserInfo user={user} />
          </header>
        </div>

        <h1 className="page-title">Followers</h1>

        {followers.length === 0 ? (
          <p className="no-results">No followers yet</p>
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

export default FollowersPage;
