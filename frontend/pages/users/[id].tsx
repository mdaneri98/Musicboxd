import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout, UserInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser,
  fetchUserByIdAsync,
  fetchFollowersAsync,
  fetchFavoriteArtistsAsync,
  fetchFavoriteAlbumsAsync,
  fetchFavoriteSongsAsync,
  fetchUserReviewsAsync,
  followUserAsync,
  unfollowUserAsync,
  selectCurrentProfile,
  selectFavoriteArtists,
  selectFavoriteAlbums,
  selectFavoriteSongs,
  selectUserReviews,
  selectLoadingProfile,
  clearCurrentProfile
} from '@/store/slices';
import type { HALResource, User } from '@/types';
import { imageRepository } from '@/repositories';

const UserProfilePage = () => {
  const router = useRouter();
  const { id } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const user = useAppSelector(selectCurrentProfile);
  const favoriteArtists = useAppSelector(selectFavoriteArtists);
  const favoriteAlbums = useAppSelector(selectFavoriteAlbums);
  const favoriteSongs = useAppSelector(selectFavoriteSongs);
  const reviews = useAppSelector(selectUserReviews);
  const loading = useAppSelector(selectLoadingProfile);
  
  const [activeTab, setActiveTab] = useState<'favorites' | 'reviews'>('favorites');
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [isFollowing, setIsFollowing] = useState(false);
  const [followLoading, setFollowLoading] = useState(false);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentProfile());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!id) return;
    
    const userId = parseInt(id as string);
    
    if (currentUser && currentUser.id === userId) {
      router.push('/profile');
      return;
    }
    
    dispatch(fetchUserByIdAsync(userId));
    dispatch(fetchFavoriteArtistsAsync({ userId }));
    dispatch(fetchFavoriteAlbumsAsync({ userId }));
    dispatch(fetchFavoriteSongsAsync({ userId }));
    
    if (isAuthenticated && currentUser) {
      dispatch(fetchFollowersAsync({ userId, page: 0, size: 100 }))
        .unwrap()
        .then((followers) => {
          setIsFollowing(followers.items.some((f: HALResource<User>) => f.data.id === currentUser.id));
        })
        .catch((err) => console.error('Failed to fetch followers:', err));
    }
  }, [id, currentUser, isAuthenticated, router, dispatch]);

  useEffect(() => {
    if (!id || activeTab !== 'reviews') return;
    
    const userId = parseInt(id as string);
    dispatch(fetchUserReviewsAsync({ userId, page: reviewsPage, size: 20 }))
      .unwrap()
      .then((reviewsData) => {
        setHasMoreReviews(reviewsData.items.length === 20);
      })
      .catch((err) => console.error('Failed to fetch reviews:', err));
  }, [id, reviewsPage, activeTab, dispatch]);

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

  if (loading || !user) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading profile...</div>
        </div>
      </Layout>
    );
  }

  const userImgUrl = user.image_id ? imageRepository.getImageUrl(user.image_id) : '/assets/default-user.png';

  return (
    <Layout title={`Musicboxd - ${user.username}'s Profile`}>
      <div className="content-wrapper">
        <UserInfo
          user={user}
          userImgUrl={userImgUrl}
          isOwnProfile={false}
          isAuthenticated={isAuthenticated}
          isFollowing={isFollowing}
          followLoading={followLoading}
          onFollowToggle={handleFollowToggle}
        />

        <div className="profile-tabs">
          <button
            className={`tab ${activeTab === 'favorites' ? 'active' : ''}`}
            onClick={() => setActiveTab('favorites')}
          >
            Favorites
          </button>
          <button
            className={`tab ${activeTab === 'reviews' ? 'active' : ''}`}
            onClick={() => setActiveTab('reviews')}
          >
            Reviews ({user.review_amount})
          </button>
        </div>

        {activeTab === 'favorites' && (
          <div className="favorites-section">
            {favoriteArtists.length > 0 && (
              <section className="entity-section">
                <h2>Favorite Artists</h2>
                <div className="entity-grid">
                  {favoriteArtists.map((artist) => (
                    <Link key={artist.id} href={`/artists/${artist.id}`} className="entity-card">
                      <img
                        src={artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png'}
                        alt={artist.name}
                        className="entity-image"
                      />
                      <h3 className="entity-name">{artist.name}</h3>
                      <p className="entity-rating">⭐ {artist.avg_rating.toFixed(1)}</p>
                    </Link>
                  ))}
                </div>
              </section>
            )}

            {favoriteAlbums.length > 0 && (
              <section className="entity-section">
                <h2>Favorite Albums</h2>
                <div className="entity-grid">
                  {favoriteAlbums.map((album) => (
                    <Link key={album.id} href={`/albums/${album.id}`} className="entity-card">
                      <img
                        src={album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png'}
                        alt={album.title}
                        className="entity-image"
                      />
                      <h3 className="entity-name">{album.title}</h3>
                      <p className="entity-artist">{album.artist_name}</p>
                      <p className="entity-rating">⭐ {album.avg_rating.toFixed(1)}</p>
                    </Link>
                  ))}
                </div>
              </section>
            )}

            {favoriteSongs.length > 0 && (
              <section className="entity-section">
                <h2>Favorite Songs</h2>
                <div className="entity-grid">
                  {favoriteSongs.map((song) => (
                    <Link key={song.id} href={`/songs/${song.id}`} className="entity-card">
                      <img
                        src={song.album_image_id ? imageRepository.getImageUrl(song.album_image_id) : '/assets/default-album.png'}
                        alt={song.title}
                        className="entity-image"
                      />
                      <h3 className="entity-name">{song.title}</h3>
                      <p className="entity-artist">{song.album_title}</p>
                      <p className="entity-rating">⭐ {song.avg_rating.toFixed(1)}</p>
                    </Link>
                  ))}
                </div>
              </section>
            )}

            {favoriteArtists.length === 0 && favoriteAlbums.length === 0 && favoriteSongs.length === 0 && (
              <div className="empty-state">
                <p>No favorites yet</p>
              </div>
            )}
          </div>
        )}

        {activeTab === 'reviews' && (
          <div className="reviews-section">
            {reviews.length > 0 ? (
              <>
                <div className="reviews-grid">
                  {reviews.map((review) => (
                    <ReviewCard key={review.id} review={review} />
                  ))}
                </div>

                <div className="pagination">
                  {reviewsPage > 1 && (
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
            ) : (
              <div className="empty-state">
                <p>No reviews yet</p>
              </div>
            )}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default UserProfilePage;
