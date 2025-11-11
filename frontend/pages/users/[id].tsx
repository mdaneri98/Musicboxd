import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout, UserInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { userRepository } from '@/repositories';
import type { Review, Artist, Album, Song, User } from '@/types';
import { imageRepository } from '@/repositories';

const UserProfilePage = () => {
  const router = useRouter();
  const { id } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const [user, setUser] = useState<User | null>(null);
  const [activeTab, setActiveTab] = useState<'favorites' | 'reviews'>('favorites');
  const [reviews, setReviews] = useState<Review[]>([]);
  const [favoriteArtists, setFavoriteArtists] = useState<Artist[]>([]);
  const [favoriteAlbums, setFavoriteAlbums] = useState<Album[]>([]);
  const [favoriteSongs, setFavoriteSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);
  const [reviewsPage, setReviewsPage] = useState(0);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [isFollowing, setIsFollowing] = useState(false);
  const [followLoading, setFollowLoading] = useState(false);

  // Fetch user data
  useEffect(() => {
    const fetchUser = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const userId = parseInt(id as string);
        
        // Check if viewing own profile
        if (currentUser && currentUser.id === userId) {
          router.push('/profile');
          return;
        }
        
        const userData = await userRepository.getUserById(userId);
        setUser(userData);
        
        // Check if following (only if authenticated)
        if (isAuthenticated && currentUser) {
          const followers = await userRepository.getFollowers(userId, 0, 100);
          setIsFollowing(followers.items.some(f => f.id === currentUser.id));
        }
      } catch (error) {
        console.error('Failed to fetch user:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id, currentUser, isAuthenticated, router]);

  // Fetch favorites
  useEffect(() => {
    const fetchFavorites = async () => {
      if (!id) return;
      
      try {
        const userId = parseInt(id as string);
        const [artists, albums, songs] = await Promise.all([
          userRepository.getFavoriteArtists(userId),
          userRepository.getFavoriteAlbums(userId),
          userRepository.getFavoriteSongs(userId),
        ]);
        
        setFavoriteArtists(artists);
        setFavoriteAlbums(albums);
        setFavoriteSongs(songs);
      } catch (error) {
        console.error('Failed to fetch favorites:', error);
      }
    };

    if (id) {
      fetchFavorites();
    }
  }, [id]);

  // Fetch reviews when tab is active
  useEffect(() => {
    const fetchReviews = async () => {
      if (!id || activeTab !== 'reviews') return;
      
      try {
        setLoading(true);
        const userId = parseInt(id as string);
        const response = await userRepository.getUserReviews(userId, reviewsPage, 20);
        setReviews(response.items);
        setHasMoreReviews(response.items.length === 20);
      } catch (error) {
        console.error('Failed to fetch reviews:', error);
      } finally {
        setLoading(false);
      }
    };

    if (id && activeTab === 'reviews') {
      fetchReviews();
    }
  }, [id, activeTab, reviewsPage]);

  const handleTabChange = (tab: 'favorites' | 'reviews') => {
    setActiveTab(tab);
    if (tab === 'reviews') {
      setReviewsPage(0);
    }
  };

  const handleFollowToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!user) return;

    try {
      setFollowLoading(true);
      if (isFollowing) {
        await userRepository.unfollowUser(user.id);
        setIsFollowing(false);
        setUser({ ...user, followersAmount: (user.followersAmount || 0) - 1 });
      } else {
        await userRepository.followUser(user.id);
        setIsFollowing(true);
        setUser({ ...user, followersAmount: (user.followersAmount || 0) + 1 });
      }
    } catch (error) {
      console.error('Failed to toggle follow:', error);
    } finally {
      setFollowLoading(false);
    }
  };

  if (loading || !user) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading user profile...</div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - @${user.username}`}>
      <div className="content-wrapper">
        {/* User Info Header */}
        <div className="profile-header">
          <header>
            <UserInfo user={user} />
          </header>

          {/* Follow/Unfollow Button */}
          <div className="user-actions">
            {!isAuthenticated ? (
              <Link href="/login" className="btn btn-primary">
                Login to Follow
              </Link>
            ) : (
              <button
                onClick={handleFollowToggle}
                disabled={followLoading}
                className={`btn ${isFollowing ? 'btn-secondary' : 'btn-primary'}`}
              >
                {followLoading ? 'Loading...' : isFollowing ? 'Unfollow' : 'Follow'}
              </button>
            )}
          </div>
        </div>

        {/* Tabs */}
        <div className="tabs">
          <span
            id="favoritesButton"
            className={`tab ${activeTab === 'favorites' ? 'active' : ''}`}
            onClick={() => handleTabChange('favorites')}
            style={{ cursor: 'pointer' }}
          >
            Favorites
          </span>
          <span
            id="reviewsButton"
            className={`tab ${activeTab === 'reviews' ? 'active' : ''}`}
            onClick={() => handleTabChange('reviews')}
            style={{ cursor: 'pointer' }}
          >
            Reviews
          </span>
        </div>

        {/* Favorites Section */}
        {activeTab === 'favorites' && (
          <section className="favorites-section">
            {/* Favorite Artists */}
            {favoriteArtists.length > 0 && (
              <>
                <h2>Favorite Artists</h2>
                <div className="carousel-container">
                  <div className="carousel">
                    {favoriteArtists.map((artist) => (
                      <div key={artist.id} className="music-item artist-item">
                        <Link href={`/artists/${artist.id}`} className="music-item-link">
                          <div className="music-item-image-container">
                            <img
                              src={artist.imageId ? imageRepository.getImageUrl(artist.imageId) : '/assets/default-artist.png'}
                              alt={artist.name}
                              className="music-item-image"
                            />
                            {artist.averageRating !== undefined && (
                              <div className="rating-badge">
                                <span className="rating">{artist.averageRating.toFixed(1)}</span>
                                <span className="star">&#9733;</span>
                              </div>
                            )}
                          </div>
                          <p className="music-item-title">{artist.name}</p>
                        </Link>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}

            {/* Favorite Albums */}
            {favoriteAlbums.length > 0 && (
              <>
                <h2>Favorite Albums</h2>
                <div className="carousel-container">
                  <div className="carousel">
                    {favoriteAlbums.map((album) => (
                      <div key={album.id} className="music-item album-item">
                        <Link href={`/albums/${album.id}`} className="music-item-link">
                          <div className="music-item-image-container">
                            <img
                              src={album.imageId ? imageRepository.getImageUrl(album.imageId) : '/assets/default-album.png'}
                              alt={album.title}
                              className="music-item-image"
                            />
                            {album.averageRating !== undefined && (
                              <div className="rating-badge">
                                <span className="rating">{album.averageRating.toFixed(1)}</span>
                                <span className="star">&#9733;</span>
                              </div>
                            )}
                          </div>
                          <p className="music-item-title">{album.title}</p>
                        </Link>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}

            {/* Favorite Songs */}
            {favoriteSongs.length > 0 && (
              <>
                <h2>Favorite Songs</h2>
                <ul className="song-list">
                  {favoriteSongs.map((song, index) => (
                    <li key={song.id}>
                      <Link href={`/songs/${song.id}`} className="song-item">
                        <span className="song-number">{index + 1}</span>
                        <span className="song-title">{song.title}</span>
                        {song.averageRating !== undefined && (
                          <div className="rating-badge">
                            <span className="rating">{song.averageRating.toFixed(1)}</span>
                            <span className="star">&#9733;</span>
                          </div>
                        )}
                      </Link>
                    </li>
                  ))}
                </ul>
              </>
            )}
          </section>
        )}

        {/* Reviews Section */}
        {activeTab === 'reviews' && (
          <section className="reviews-section">
            {loading ? (
              <div className="loading">Loading reviews...</div>
            ) : reviews.length === 0 ? (
              <div className="empty-state">
                <h3>No reviews found</h3>
              </div>
            ) : (
              <>
                <div className="reviews-grid">
                  {reviews.map((review) => (
                    <ReviewCard key={review.id} review={review} />
                  ))}
                </div>

                {/* Pagination */}
                <div className="pagination">
                  {reviewsPage > 0 && (
                    <button
                      onClick={() => setReviewsPage(reviewsPage - 1)}
                      className="btn btn-secondary"
                    >
                      Previous Page
                    </button>
                  )}
                  {hasMoreReviews && (
                    <button
                      onClick={() => setReviewsPage(reviewsPage + 1)}
                      className="btn btn-secondary"
                    >
                      Next Page
                    </button>
                  )}
                </div>
              </>
            )}
          </section>
        )}
      </div>
    </Layout>
  );
};

export default UserProfilePage;

