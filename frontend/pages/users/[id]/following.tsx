import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { Layout, UserInfo } from '@/components/layout';
import { UserCard } from '@/components/cards';
import { userRepository } from '@/repositories';
import type { HALResource, User } from '@/types';

const FollowingPage = () => {
  const router = useRouter();
  const { id } = router.query;
  
  const [user, setUser] = useState<User | null>(null);
  const [following, setFollowing] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const userId = parseInt(id as string);
        
        // Fetch user and following in parallel
        const [userData, followingData] = await Promise.all([
          userRepository.getUserById(userId),
          userRepository.getFollowing(userId, page, 20),
        ]);
        
        setUser(userData.data as User);
        setFollowing(followingData.items.map((item: HALResource<User>) => item.data as User));
        setHasMore(followingData.items.length === 20);
      } catch (error) {
        console.error('Failed to fetch following:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, page]);

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
    <Layout title={`Musicboxd - @${user.username} Following`}>
      <div className="content-wrapper">
        {/* User Info Header */}
        <div className="profile-header">
          <header>
            <UserInfo user={user} />
          </header>
        </div>

        {/* Section Title */}
        <h1 className="page-title">Following</h1>

        {/* Users Grid */}
        {following.length === 0 ? (
          <p className="no-results">Not following anyone yet</p>
        ) : (
          <>
            <div className="users-grid">
              {following.map((user) => (
                <UserCard key={user.id} user={user} />
              ))}
            </div>

            {/* Pagination */}
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

