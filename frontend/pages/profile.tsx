import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync, selectUserReviewsPagination, fetchFavoriteArtistsAsync, fetchFavoriteAlbumsAsync, fetchFavoriteSongsAsync, fetchUserReviewsAsync, selectFavoriteArtists, selectFavoriteAlbums, selectFavoriteSongs, selectUserReviews, selectLoadingFavorites, selectLoadingReviews } from '@/store/slices';
import { ProfileTabEnum } from '@/types';
import { ArtistCard, AlbumCard, SongCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';

const ProfilePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { tab: queryTab } = router.query;

  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const [activeTab, setActiveTab] = useState<ProfileTabEnum>(queryTab ? queryTab as ProfileTabEnum : ProfileTabEnum.FAVORITES);
  const favoriteArtists = useAppSelector(selectFavoriteArtists);
  const favoriteAlbums = useAppSelector(selectFavoriteAlbums);
  const favoriteSongs = useAppSelector(selectFavoriteSongs);
  const reviews = useAppSelector(selectUserReviews);
  const loadingFavorites = useAppSelector(selectLoadingFavorites);
  const loadingReviews = useAppSelector(selectLoadingReviews);
  const pagination = useAppSelector(selectUserReviewsPagination); 


  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!currentUser) {
      dispatch(getCurrentUserAsync());
    }
  }, [isAuthenticated, currentUser, dispatch, router]);

  useEffect(() => {
    if (currentUser) {
      dispatch(fetchFavoriteArtistsAsync(currentUser.id));
      dispatch(fetchFavoriteAlbumsAsync(currentUser.id));
      dispatch(fetchFavoriteSongsAsync(currentUser.id));
      dispatch(fetchUserReviewsAsync({ userId: currentUser.id, page: pagination.page, size: pagination.size }));
    }
  }, [currentUser, dispatch]);

  const handleTabChange = (tab: ProfileTabEnum) => {
    setActiveTab(tab);
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
            <UserInfo user={currentUser} isOwnProfile={true} isAuthenticated={isAuthenticated} isFollowing={false} followLoading={false} onFollowToggle={() => {}} />
          </header>
        </div>

        {/* Tabs */}
        <div className="tabs">
          <span
            id="favoritesButton"
            className={`tab ${activeTab === ProfileTabEnum.FAVORITES ? 'active' : ''}`}
            onClick={() => handleTabChange(ProfileTabEnum.FAVORITES)}
            style={{ cursor: 'pointer' }}
          >
            {t('profile.tabs.favorites')}
          </span>
          <span
            id="reviewsButton"
            className={`tab ${activeTab === ProfileTabEnum.REVIEWS ? 'active' : ''}`}
            onClick={() => handleTabChange(ProfileTabEnum.REVIEWS)}
            style={{ cursor: 'pointer' }}
          >
            {t('profile.tabs.reviews')}
          </span>
        </div>

        {/* Favorites Section */}
        {loadingFavorites ? (
          <div className="loading">{t('profile.loadingFavorites')}
          <LoadingSpinner size="large" />
          </div>
        ) : (
          activeTab === ProfileTabEnum.FAVORITES && (
          <section className="favorites-section">
                        {/* Favorite Artists */}
                        <h2>{t('profile.favoriteArtists')}</h2>
            {Object.values(favoriteArtists).length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">{t('profile.addFavoriteArtists')}</p>
              </div>
            ) : (
              <div className="carousel-container">
                <div className="carousel">
                  {Object.values(favoriteArtists).map((artist) => (
                    <ArtistCard key={artist.id} artist={artist} />
                  ))}
                </div>
              </div>
            )}

            {/* Favorite Albums */}
            <h2>{t('profile.favoriteAlbums')}</h2>
            {Object.values(favoriteAlbums).length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">{t('profile.addFavoriteAlbums')}</p>
              </div>
            ) : (
              <div className="carousel-container">
                <div className="carousel">
                  {Object.values(favoriteAlbums).map((album) => (
                    <AlbumCard key={album.id} album={album} />
                  ))}
                </div>
              </div>
            )}

            {/* Favorite Songs */}
            <h2>{t('profile.favoriteSongs')}</h2>
            {Object.values(favoriteSongs).length === 0 ? (
              <div className="empty-state">
                <p className="add-favorites">{t('profile.addFavoriteSongs')}</p>
              </div>
            ) : (
              <ul className="song-list">
                {Object.values(favoriteSongs).map((song, index) => (
                  <SongCard key={song.id} song={song} index={index} />
                ))}
              </ul>
            )}
          </section>
        ))}
        {/* Reviews Section */}
        {activeTab === ProfileTabEnum.REVIEWS && (
          <section className="reviews-section">
            {loadingReviews ? (
              <div className="loading">{t('profile.loadingReviews')}</div>
            ) : Object.values(reviews).length === 0 ? (
              <div className="empty-state">
                <h3>{t('profile.noReviews')}</h3>
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
                  {pagination.page > 1 && (
                    <button
                      onClick={() => dispatch(fetchUserReviewsAsync({ userId: currentUser.id, page: pagination.page - 1, size: pagination.size }))}
                      className="btn btn-secondary"
                    >
                      {t('common.previous')} {t('profile.page')}
                    </button>
                  )}
                  {pagination.totalCount > pagination.page * pagination.size && (
                    <button
                      onClick={() => dispatch(fetchUserReviewsAsync({ userId: currentUser.id, page: pagination.page + 1, size: pagination.size }))}
                      className="btn btn-secondary"
                    >
                      {t('common.next')} {t('profile.page')}
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

