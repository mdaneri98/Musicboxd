import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, UserInfo } from '@/components/layout';
import { ReviewCard, ArtistCard, AlbumCard, SongCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import { 
  selectIsAuthenticated, 
  fetchUserByIdAsync,
  fetchFavoriteArtistsAsync,
  fetchFavoriteAlbumsAsync,
  fetchFavoriteSongsAsync,
  fetchUserReviewsAsync,
  fetchMoreUserReviewsAsync,
  followUserAsync,
  unfollowUserAsync,
  selectCurrentUser,
  selectUserById,
  selectFavoriteArtists,
  selectFavoriteAlbums,
  selectFavoriteSongs,
  selectUserReviews,
  selectUserReviewsPagination,
  selectUserReviewsHasMore,
  clearCurrentProfile,
  selectLoadingReviews,
  selectLoadingMoreReviews,
  selectLoadingFavorites
} from '@/store/slices';
import { ProfileTabEnum } from '@/types';

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
  const loadingReviews = useAppSelector(selectLoadingReviews);
  const loadingMoreReviews = useAppSelector(selectLoadingMoreReviews);
  const reviewsPagination = useAppSelector(selectUserReviewsPagination);
  const hasMoreReviews = useAppSelector(selectUserReviewsHasMore);
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
    dispatch(fetchUserReviewsAsync({ userId: userIdNum, page: 1, size: 20 }));
  }, [userId, dispatch, loggedUser, router, activeTab]);

  // Update isFollowing when user data is loaded
  useEffect(() => {
    if (user) {
      setIsFollowing(user.followed ?? false);
    }
  }, [user]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!userId || !hasMoreReviews || loadingMoreReviews) return;
    
    const userIdNum = parseInt(userId as string);
    const nextPage = reviewsPagination.page + 1;
    
    await dispatch(fetchMoreUserReviewsAsync({ 
      userId: userIdNum, 
      page: nextPage, 
      size: reviewsPagination.size 
    }));
  }, [dispatch, userId, reviewsPagination.page, reviewsPagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loadingReviews || loadingMoreReviews,
    enabled: activeTab === ProfileTabEnum.REVIEWS && !!user && !loadingReviews,
  });

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

  if (loadingFavorites || (loadingReviews && !user)) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" message={t('userProfile.loadingProfile')} />
        </div>
      </Layout>
    );
  }

  if (!user) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
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
          <div className="loading-container">
            <LoadingSpinner size="large" message={t('profile.loadingFavorites')} />
          </div>
        ) : (
          activeTab === ProfileTabEnum.FAVORITES && (
          <section className="favorites-section">
            {/* Favorite Artists */}
            {favoriteArtists.length > 0 && (
              <div>
                <h2>{t('profile.favoriteArtists')}</h2>
                <div className="carousel-container">
                  <div className="carousel">
                    {favoriteArtists.map((artist) => (
                      <ArtistCard key={artist.id} artist={artist} />
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Favorite Albums */}
            {favoriteAlbums.length > 0 && (
              <div>
                <h2>{t('profile.favoriteAlbums')}</h2>    
                <div className="carousel-container">
                  <div className="carousel">
                    {favoriteAlbums.map((album) => (
                      <AlbumCard key={album.id} album={album} />
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Favorite Songs */}
            {favoriteSongs.length > 0 && (
              <div>
                <h2>{t('profile.favoriteSongs')}</h2>
                <ul className="song-list">
                  {favoriteSongs.map((song, index) => (
                    <SongCard key={song.id} song={song} index={index} />
                  ))}
                </ul>
              </div>
            )}
          </section>
        ))}

        {/* Reviews Section */}
        {activeTab === ProfileTabEnum.REVIEWS && (
          <section className="reviews-section">
            {loadingReviews && reviews.length === 0 ? (
              <LoadingSpinner size="large" message={t('profile.loadingReviews')} />
            ) : reviews.length === 0 ? (
              <div className="empty-state">
                <h3>{t('profile.noReviews')}</h3>
              </div>
            ) : (
              <>
                <div className="reviews-grid">
                  {reviews.map((review) => (
                    <ReviewCard key={review.id} review={review} />
                  ))}
                </div>

                {/* Sentinel element for infinite scroll */}
                <div ref={sentinelRef} className="infinite-scroll-sentinel" />

                {/* Loading indicator for more content */}
                {(loadingMoreReviews || isFetchingMore) && (
                  <div className="loading-more">
                    <LoadingSpinner size="small" />
                  </div>
                )}

                {/* End of content message */}
                {!hasMoreReviews && reviews.length > 0 && (
                  <div className="end-of-content">
                    <p>{t('common.noMoreContent')}</p>
                  </div>
                )}
              </>
            )}
          </section>
        )}
      </div>
    </Layout>
  );
};

export default UserProfilePage;
