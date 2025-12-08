/**
 * Home Page (Authenticated)
 * Main feed page for authenticated users with For You / Following tabs
 * Migrated from: home.jsp
 */

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, fetchReviewsAsync, selectOrderedReviews, selectLoadingReviews, selectReviewPagination } from '@/store/slices';
import { FilterTypeEnum, HomeTabEnum  } from '@/types';

const HomePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const reviews = useAppSelector(selectOrderedReviews);
  const loadingReviews = useAppSelector(selectLoadingReviews);
  const pagination = useAppSelector(selectReviewPagination);
  const [activeTab, setActiveTab] = useState<HomeTabEnum>(HomeTabEnum.FOR_YOU);
  const [filter, setFilter] = useState<FilterTypeEnum>(activeTab === HomeTabEnum.FOR_YOU ? FilterTypeEnum.LIKES : FilterTypeEnum.FOLLOWING);

  useEffect(() => {
    // Redirect to landing page if not authenticated
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        await dispatch(fetchReviewsAsync({ page:1, size: pagination.size, filter: filter })).unwrap();
      } catch (error) {
        console.error('Failed to fetch reviews:', error);
      }
    };

    fetchReviews();
  }, [isAuthenticated, activeTab, filter, dispatch]);

  const handleTabChange = (tab: HomeTabEnum) => {
    setActiveTab(tab);
    setFilter(tab === HomeTabEnum.FOR_YOU ? FilterTypeEnum.LIKES : FilterTypeEnum.FOLLOWING);
  };

  if (!isAuthenticated) {
    return null; // Will redirect
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
          {loadingReviews ? (
            <div className="loading">{t('home.loadingReviews')}</div>
          ) : Object.values(reviews).length === 0 ? (
            <div className="empty-state">
              <h3>{t('home.noReviews')}</h3>
              <h4>{t('home.noReviewsHint')}</h4>
            </div>
          ) : (
            <>
              <div className="reviews-grid">
                {Object.values(reviews).map((review) => (
                  <ReviewCard key={review.id} review={review} />
                ))}
              </div>

              {/* Paginación */}
              <div className="pagination">
                {pagination.page > 1 && (
                  <button
                    onClick={() => dispatch(fetchReviewsAsync({ page: pagination.page - 1, size: pagination.size, filter: filter }))}
                    className="btn btn-secondary"
                  >
                    {t('common.previous')} {t('home.page')}
                  </button>
                )}
                {pagination.totalCount > pagination.page * pagination.size && (
                  <button
                    onClick={() => dispatch(fetchReviewsAsync({ page: pagination.page + 1, size: pagination.size, filter: filter }))}
                    className="btn btn-primary"
                  >
                    {t('common.next')} {t('home.page')}
                  </button>
                )}
              </div>
            </>
          )}
        </section>
      </div>
    </Layout>
  );
};

export default HomePage;

