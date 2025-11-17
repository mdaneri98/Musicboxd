import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout, UserInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  fetchUserByIdAsync,
  fetchFavoriteArtistsAsync,
  fetchFavoriteAlbumsAsync,
  fetchFavoriteSongsAsync,
  fetchUserReviewsAsync,
  followUserAsync,
  unfollowUserAsync,
  selectCurrentUser,
  selectUserById,
  selectFavoriteArtists,
  selectFavoriteAlbums,
  selectFavoriteSongs,
  selectUserReviews,
  selectUserReviewsPagination,
  clearCurrentProfile,
  selectLoadingReviews,
  selectLoadingFavorites
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import { ProfileTabEnum } from '@/types';

const UserProfilePage = () => {
  const router = useRouter();
  const { id, tab: queryTab } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const loggedUser = useAppSelector(selectCurrentUser);
  const user = useAppSelector(selectUserById(parseInt(id as string)));
  
  const [activeTab, setActiveTab] = useState<ProfileTabEnum>(queryTab ? queryTab as ProfileTabEnum : ProfileTabEnum.FAVORITES);
  const favoriteArtists = useAppSelector(selectFavoriteArtists);
  const favoriteAlbums = useAppSelector(selectFavoriteAlbums);
  const favoriteSongs = useAppSelector(selectFavoriteSongs);
  const reviews = useAppSelector(selectUserReviews);
  const loadingFavorites = useAppSelector(selectLoadingFavorites);
  const   loadingReviews = useAppSelector(selectLoadingReviews);
  const reviewsPagination = useAppSelector(selectUserReviewsPagination); 
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

    if (loggedUser && loggedUser.id === userId) {
      router.push(`/profile?tab=${activeTab}`);
      return;
    }
    
    dispatch(fetchUserByIdAsync(userId));
    dispatch(fetchFavoriteArtistsAsync(userId));
    dispatch(fetchFavoriteAlbumsAsync(userId));
    dispatch(fetchFavoriteSongsAsync(userId));
    dispatch(fetchUserReviewsAsync({ userId, page: reviewsPagination.page, size: reviewsPagination.size }))
    setIsFollowing(user?.is_followed_by_logged_user ?? false);
  }, [id, dispatch]);


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

  const handleTabChange = (tab: ProfileTabEnum) => {
    setActiveTab(tab);
  };

  if (loadingFavorites || loadingReviews || !user) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading profile...</div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - ${user.username}'s Profile`}>
      <div className="content-wrapper">
        <UserInfo
          user={user}
          isOwnProfile={false}
          isAuthenticated={isAuthenticated}
          isFollowing={isFollowing}
          followLoading={followLoading}
          onFollowToggle={handleFollowToggle}
        />

        {/* Tabs */}
        <div className="tabs">
          <span
            id="favoritesButton"
            className={`tab ${activeTab === ProfileTabEnum.FAVORITES ? 'active' : ''}`}
            onClick={() => handleTabChange(ProfileTabEnum.FAVORITES)}
            style={{ cursor: 'pointer' }}
          >
            Favorites
          </span>
          <span
            id="reviewsButton"
            className={`tab ${activeTab === ProfileTabEnum.REVIEWS ? 'active' : ''}`}
            onClick={() => handleTabChange(ProfileTabEnum.REVIEWS)}
            style={{ cursor: 'pointer' }}
          >
            Reviews
          </span>
        </div>

        {/* Favorites Section */}
        {loadingFavorites ? (
          <div className="loading">Loading favorites...</div>
        ) : (
          activeTab === ProfileTabEnum.FAVORITES && (
          <section className="favorites-section">
            {/* Favorite Artists */}
            <h2>Favorite Artists</h2>
            {Object.values(favoriteArtists).length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">Add up to 5 favorite artists to your profile</p>
              </div>
            ) : (
              <div className="carousel-container">
                <div className="carousel">
                  {Object.values(favoriteArtists).map((artist) => (
                    <div key={artist.id} className="music-item artist-item">
                      <Link href={`/artists/${artist.id}`} className="music-item-link">
                        <div className="music-item-image-container">
                          <img
                            src={artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png'}
                            alt={artist.name}
                            className="music-item-image"
                          />
                          {artist.avg_rating !== undefined && (
                            <div className="rating-badge">
                              <span className="rating">{artist.avg_rating.toFixed(1)}</span>
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
            )}

            {/* Favorite Albums */}
            <h2>Favorite Albums</h2>
            {Object.values(favoriteAlbums).length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">Add up to 5 favorite albums to your profile</p>
              </div>
            ) : (
              <div className="carousel-container">
                <div className="carousel">
                  {Object.values(favoriteAlbums).map((album) => (
                    <div key={album.id} className="music-item album-item">
                      <Link href={`/albums/${album.id}`} className="music-item-link">
                        <div className="music-item-image-container">
                          <img
                            src={album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png'}
                            alt={album.title}
                            className="music-item-image"
                          />
                          {album.avg_rating !== undefined && (
                            <div className="rating-badge">
                              <span className="rating">{album.avg_rating.toFixed(1)}</span>
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
            )}

            {/* Favorite Songs */}
            <h2>Favorite Songs</h2>
            {Object.values(favoriteSongs).length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">Add up to 5 favorite songs to your profile</p>
              </div>
            ) : (
              <ul className="song-list">
                {Object.values(favoriteSongs).map((song, index) => (
                  <li key={song.id}>
                    <Link href={`/songs/${song.id}`} className="song-item">
                      <span className="song-number">{index + 1}</span>
                      <span className="song-title">{song.title}</span>
                      {song.avg_rating !== 0 && (
                        <div className="rating-badge">
                          <span className="rating">{song.avg_rating.toFixed(1)}</span>
                          <span className="star">&#9733;</span>
                        </div>
                      )}
                    </Link>
                  </li>
                ))}
              </ul>
            )}
          </section>
        ))}

        {/* Reviews Section */}
        {activeTab === ProfileTabEnum.REVIEWS && (
          <section className="reviews-section">
            {loadingReviews ? (
              <div className="loading">Loading reviews...</div>
            ) : Object.values(reviews).length === 0 ? (
              <div className="empty-state">
                <h3>No reviews found</h3>
              </div>
            ) : (
              <>
                <div className="reviews-grid">
                  {Object.values(reviews).map((review) => (
                    <ReviewCard key={review.id} review={review} />
                  ))}
                </div>

                {/* Pagination */}
                <div className="pagination">
                  {reviewsPagination.page > 1 && (
                    <button
                      onClick={() => dispatch(fetchUserReviewsAsync({ userId: user.id, page: reviewsPagination.page - 1, size: reviewsPagination.size }))}
                      className="btn btn-secondary"
                    >
                      Previous Page
                    </button>
                  )}
                  {reviewsPagination.totalCount > reviewsPagination.page * reviewsPagination.size && (
                    <button
                      onClick={() => dispatch(fetchUserReviewsAsync({ userId: user.id, page: reviewsPagination.page + 1, size: reviewsPagination.size }))}
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
