

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
  fetchReviewsAsync, 
  fetchMoreReviewsAsync,
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
  const reviews = useAppSelector(selectOrderedReviews);
  const loading = useAppSelector(selectReviewLoading);
  const loadingMore = useAppSelector(selectReviewLoadingMore);
  const pagination = useAppSelector(selectReviewPagination);
  const hasMore = useAppSelector(selectReviewsHasMore);
  
  const [activeTab, setActiveTab] = useState<HomeTabEnum>(HomeTabEnum.FOR_YOU);
  const filter = activeTab === HomeTabEnum.FOR_YOU ? FilterTypeEnum.LIKES : FilterTypeEnum.FOLLOWING;

  // Redirect to landing page if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  // Initial data fetch when tab/filter changes
  useEffect(() => {
    if (!isAuthenticated) return;
    
    // Clear existing reviews and fetch fresh data
    dispatch(clearReviews());
    dispatch(fetchReviewsAsync({ page: 1, size: 10, filter }));
  }, [isAuthenticated, filter, dispatch]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!hasMore || loadingMore) return;
    
    const nextPage = pagination.page + 1;
    await dispatch(fetchMoreReviewsAsync({ 
      page: nextPage, 
      size: pagination.size, 
      filter 
    }));
  }, [dispatch, pagination.page, pagination.size, filter, hasMore, loadingMore]);

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
            <LoadingSpinner size="large" message={t('home.loadingReviews')} />
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
