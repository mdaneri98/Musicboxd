import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
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
import { ProfileTabEnum } from '@/types';
import { ArtistCard, AlbumCard, SongCard } from '@/components/cards';

const UserProfilePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { userId, tab: queryTab } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const loggedUser = useAppSelector(selectCurrentUser);
  const user = useAppSelector(selectUserById(parseInt(userId as string)));
  
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
    if (!userId) return;

    const userIdNum = parseInt(userId as string);

    if (loggedUser && loggedUser.id === userIdNum) {
      router.push(`/profile?tab=${activeTab}`);
      return;
    }
    
    dispatch(fetchUserByIdAsync(userIdNum));
    dispatch(fetchFavoriteArtistsAsync(userIdNum));
    dispatch(fetchFavoriteAlbumsAsync(userIdNum));
    dispatch(fetchFavoriteSongsAsync(userIdNum));
    dispatch(fetchUserReviewsAsync({ userId: userIdNum, page: reviewsPagination.page, size: reviewsPagination.size }))
    setIsFollowing(user?.followed ?? false);
  }, [userId, dispatch]);


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
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <div className="loading">{t('userProfile.loadingProfile')}</div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - ${user.username}'s ${t('userProfile.profile')}`}>
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
          <div className="loading">{t('profile.loadingFavorites')}</div>
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
                  {reviewsPagination.page > 1 && (
                    <button
                      onClick={() => dispatch(fetchUserReviewsAsync({ userId: user.id, page: reviewsPagination.page - 1, size: reviewsPagination.size }))}
                      className="btn btn-secondary"
                    >
                      {t('userProfile.previousPage')}
                    </button>
                  )}
                  {reviewsPagination.totalCount > reviewsPagination.page * reviewsPagination.size && (
                    <button
                      onClick={() => dispatch(fetchUserReviewsAsync({ userId: user.id, page: reviewsPagination.page + 1, size: reviewsPagination.size }))}
                      className="btn btn-secondary"
                    >
                      {t('userProfile.nextPage')}
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
