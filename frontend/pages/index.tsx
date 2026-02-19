


import { useEffect, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import {
  selectIsAuthenticated,
  selectAuthInitializing,
  selectUserId,
  fetchReviewsAsync,
  fetchMoreReviewsAsync,
  fetchFollowingReviewsAsync,
  fetchMoreFollowingReviewsAsync,
  fetchReviewLikedStatusAsync,
  clearReviews,
  selectOrderedReviews,
  selectReviewLoading,
  selectReviewLoadingMore,
  selectReviewPagination,
  selectReviewsHasMore,
} from '@/store/slices';
import { FilterTypeEnum, HomeTabEnum } from '@/types';
import { useState } from 'react';

const HomePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const authInitializing = useAppSelector(selectAuthInitializing);
  const userId = useAppSelector(selectUserId);
  const reviews = useAppSelector(selectOrderedReviews);
  const loading = useAppSelector(selectReviewLoading);
  const loadingMore = useAppSelector(selectReviewLoadingMore);
  const pagination = useAppSelector(selectReviewPagination);
  const hasMore = useAppSelector(selectReviewsHasMore);

  const [activeTab, setActiveTab] = useState<HomeTabEnum>(HomeTabEnum.FOR_YOU);
  const isFollowingTab = activeTab === HomeTabEnum.FOLLOWING;

  // Redirect to landing page if not authenticated (after auth initialization is complete)
  useEffect(() => {
    // Wait for auth to finish initializing before making redirect decision
    if (authInitializing) return;

    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [authInitializing, isAuthenticated, router]);

  // Initial data fetch when tab changes
  useEffect(() => {
    if (!isAuthenticated) return;

    // Clear existing reviews and fetch fresh data
    dispatch(clearReviews());
    if (isFollowingTab && userId) {
      dispatch(fetchFollowingReviewsAsync({ userId, page: 1, size: 10 }));
    } else {
      dispatch(fetchReviewsAsync({ page: 1, size: 10, filter: FilterTypeEnum.LIKES }));
    }
  }, [isAuthenticated, isFollowingTab, userId, dispatch]);

  // Batch fetch liked status for visible reviews
  useEffect(() => {
    if (!isAuthenticated || !userId || reviews.length === 0) return;

    const idsWithoutLikedStatus = reviews
      .filter((r) => r.liked === undefined)
      .map((r) => r.id);

    if (idsWithoutLikedStatus.length > 0) {
      dispatch(fetchReviewLikedStatusAsync({ reviewIds: idsWithoutLikedStatus, userId }));
    }
  }, [isAuthenticated, userId, reviews, dispatch]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!hasMore || loadingMore) return;

    const nextPage = pagination.page + 1;
    if (isFollowingTab && userId) {
      await dispatch(fetchMoreFollowingReviewsAsync({
        userId,
        page: nextPage,
        size: pagination.size,
      }));
    } else {
      await dispatch(fetchMoreReviewsAsync({
        page: nextPage,
        size: pagination.size,
        filter: FilterTypeEnum.LIKES,
      }));
    }
  }, [dispatch, pagination.page, pagination.size, isFollowingTab, userId, hasMore, loadingMore]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore,
    isLoading: loading || loadingMore,
    enabled: isAuthenticated && !loading,
  });

  const handleTabChange = (tab: HomeTabEnum) => {
    if (tab !== activeTab) {
      setActiveTab(tab);
    }
  };

  if (!isAuthenticated) {
    return null;
  }

  return (
    <Layout title="Musicboxd - Home">
      <div className="content-wrapper">
        {/* Tabs de navegación */}
        <div className="section-header-home">
          <div className="tabs">
            <span
              className={`tab ${activeTab === HomeTabEnum.FOR_YOU ? 'active' : ''}`}
              onClick={() => handleTabChange(HomeTabEnum.FOR_YOU)}
              style={{ cursor: 'pointer' }}
            >
              {t('home.tabs.forYou')}
            </span>
            <span
              className={`tab ${activeTab === HomeTabEnum.FOLLOWING ? 'active' : ''}`}
              onClick={() => handleTabChange(HomeTabEnum.FOLLOWING)}
              style={{ cursor: 'pointer' }}
            >
              {t('home.tabs.following')}
            </span>
          </div>
        </div>

        {/* Contenido principal */}
        <section className="reviews-section">
          {loading && reviews.length === 0 ? (
            <LoadingSpinner size="large" />
          ) : reviews.length === 0 ? (
            <div className="empty-state">
              <h3>{t('home.noReviews')}</h3>
              <h4>{t('home.noReviewsHint')}</h4>
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
              {(loadingMore || isFetchingMore) && (
                <div className="loading-more">
                  <LoadingSpinner size="small" />
                </div>
              )}

              {/* End of content message */}
              {!hasMore && reviews.length > 0 && (
                <div className="end-of-content">
                  <p>{t('common.noMoreContent')}</p>
                </div>
              )}
            </>
          )}
        </section>
      </div>
    </Layout>
  );
};

export default HomePage;
