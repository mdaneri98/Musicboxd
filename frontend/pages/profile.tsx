import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout, UserInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync } from '@/store/slices';
import { userRepository } from '@/repositories';
import type { Review, Artist, Album, Song } from '@/types';
import { imageRepository } from '@/repositories';

const ProfilePage = () => {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const [activeTab, setActiveTab] = useState<'favorites' | 'reviews'>('favorites');
  const [reviews, setReviews] = useState<Review[]>([]);
  const [favoriteArtists, setFavoriteArtists] = useState<Artist[]>([]);
  const [favoriteAlbums, setFavoriteAlbums] = useState<Album[]>([]);
  const [favoriteSongs, setFavoriteSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!currentUser) {
      dispatch(getCurrentUserAsync());
    }
  }, [isAuthenticated, currentUser, dispatch, router]);

  // Fetch favorites
  useEffect(() => {
    const fetchFavorites = async () => {
      if (!currentUser) return;
      
      try {
        setLoading(true);
        const [artists, albums, songs] = await Promise.all([
          userRepository.getFavoriteArtists(currentUser.id),
          userRepository.getFavoriteAlbums(currentUser.id),
          userRepository.getFavoriteSongs(currentUser.id),
        ]);
        
        setFavoriteArtists(artists.items.map((item) => item.data));
        setFavoriteAlbums(albums.items.map((item) => item.data));
        setFavoriteSongs(songs.items.map((item) => item.data));
      } catch (error) {
        console.error('Failed to fetch favorites:', error);
      } finally {
        setLoading(false);
      }
    };

    if (currentUser) {
      fetchFavorites();
    }
  }, [currentUser]);

  // Fetch reviews when tab is active
  useEffect(() => {
    const fetchReviews = async () => {
      if (!currentUser || activeTab !== 'reviews') return;
      
      try {
        setLoading(true);
        const response = await userRepository.getUserReviews(currentUser.id, reviewsPage, 20);
        setReviews(response.items.map((item) => item.data));
        setHasMoreReviews(response.items.length === 20);
      } catch (error) {
        console.error('Failed to fetch reviews:', error);
      } finally {
        setLoading(false);
      }
    };

    if (currentUser && activeTab === 'reviews') {
      fetchReviews();
    }
  }, [currentUser, activeTab, reviewsPage]);

  const handleTabChange = (tab: 'favorites' | 'reviews') => {
    setActiveTab(tab);
    if (tab === 'reviews') {
      setReviewsPage(1);
    }
  };

  if (!isAuthenticated || !currentUser) {
    return null; // Will redirect
  }

  return (
    <Layout title="Musicboxd - My Profile">
      <div className="content-wrapper">
        {/* User Info Header */}
        <div className="profile-header">
          <header>
            <UserInfo user={currentUser} />
          </header>

          {/* Edit Profile Button */}
          <Link href="/profile/edit" className="btn btn-primary">
            Edit Profile
          </Link>
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
            <h2>Favorite Artists</h2>
            {favoriteArtists.length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">Add up to 5 favorite artists to your profile</p>
              </div>
            ) : (
              <div className="carousel-container">
                <div className="carousel">
                  {favoriteArtists.map((artist) => (
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
            {favoriteAlbums.length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">Add up to 5 favorite albums to your profile</p>
              </div>
            ) : (
              <div className="carousel-container">
                <div className="carousel">
                  {favoriteAlbums.map((album) => (
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
            {favoriteSongs.length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">Add up to 5 favorite songs to your profile</p>
              </div>
            ) : (
              <ul className="song-list">
                {favoriteSongs.map((song, index) => (
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
            )}
          </section>
        )}
      </div>
    </Layout>
  );
};

export default ProfilePage;

