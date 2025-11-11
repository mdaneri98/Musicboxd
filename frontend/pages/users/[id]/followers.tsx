import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { Layout, UserInfo } from '@/components/layout';
import { UserCard } from '@/components/cards';
import { userRepository } from '@/repositories';
import type { User } from '@/types';

const FollowersPage = () => {
  const router = useRouter();
  const { id } = router.query;
  
  const [user, setUser] = useState<User | null>(null);
  const [followers, setFollowers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const userId = parseInt(id as string);
        
        // Fetch user and followers in parallel
        const [userData, followersData] = await Promise.all([
          userRepository.getUserById(userId),
          userRepository.getFollowers(userId, page, 20),
        ]);
        
        setUser(userData);
        setFollowers(followersData.items);
        setHasMore(followersData.items.length === 20);
      } catch (error) {
        console.error('Failed to fetch followers:', error);
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
    <Layout title={`Musicboxd - @${user.username} Followers`}>
      <div className="content-wrapper">
        {/* User Info Header */}
        <div className="profile-header">
          <header>
            <UserInfo user={user} />
          </header>
        </div>

        {/* Section Title */}
        <h1 className="page-title">Followers</h1>

        {/* Users Grid */}
        {followers.length === 0 ? (
          <p className="no-results">No followers yet</p>
        ) : (
          <>
            <div className="users-grid">
              {followers.map((follower) => (
                <UserCard key={follower.id} user={follower} />
              ))}
            </div>

            {/* Pagination */}
            <div className="pagination">
              {page > 0 && (
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

