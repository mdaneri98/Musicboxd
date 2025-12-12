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
  selectCurrentUser, 
  getCurrentUserAsync, 
  selectUserReviewsPagination, 
  fetchFavoriteArtistsAsync, 
  fetchFavoriteAlbumsAsync, 
  fetchFavoriteSongsAsync, 
  fetchUserReviewsAsync, 
  fetchMoreUserReviewsAsync,
  selectFavoriteArtists, 
  selectFavoriteAlbums, 
  selectFavoriteSongs, 
  selectUserReviews, 
  selectLoadingFavorites, 
  selectLoadingReviews,
  selectLoadingMoreReviews,
  selectUserReviewsHasMore,
} from '@/store/slices';
import { ProfileTabEnum } from '@/types';

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
  const loadingMoreReviews = useAppSelector(selectLoadingMoreReviews);
  const pagination = useAppSelector(selectUserReviewsPagination);
  const hasMoreReviews = useAppSelector(selectUserReviewsHasMore);

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
      dispatch(fetchUserReviewsAsync({ userId: currentUser.id, page: 1, size: 10 }));
    }
  }, [currentUser, dispatch]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!currentUser || !hasMoreReviews || loadingMoreReviews) return;
    
    const nextPage = pagination.page + 1;
    await dispatch(fetchMoreUserReviewsAsync({ 
      userId: currentUser.id, 
      page: nextPage, 
      size: pagination.size 
    }));
  }, [dispatch, currentUser, pagination.page, pagination.size, hasMoreReviews, loadingMoreReviews]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore: hasMoreReviews,
    isLoading: loadingReviews || loadingMoreReviews,
    enabled: activeTab === ProfileTabEnum.REVIEWS && !!currentUser && !loadingReviews,
  });

  const handleTabChange = (tab: ProfileTabEnum) => {
    setActiveTab(tab);
  };

  if (!isAuthenticated || !currentUser) {
    return null;
  }

  return (
    <Layout title="Musicboxd - My Profile">
      <div className="content-wrapper">
        {/* User Info Header */}
        <UserInfo user={currentUser} isOwnProfile={true} isAuthenticated={isAuthenticated} isFollowing={false} followLoading={false} onFollowToggle={() => {}} />

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
          <div className="loading">
            {t('profile.loadingFavorites')}
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

export default ProfilePage;
