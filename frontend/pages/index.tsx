/**
 * Home Page (Authenticated)
 * Main feed page for authenticated users with For You / Following tabs
 * Migrated from: home.jsp
 */

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, fetchReviewsAsync, selectReviews, selectLoadingReviews, selectReviewPagination } from '@/store/slices';
import { FilterTypeEnum, HomeTabEnum  } from '@/types';

const HomePage = () => {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const reviews = useAppSelector(selectReviews);
  const loadingReviews = useAppSelector(selectLoadingReviews);
  const pagination = useAppSelector(selectReviewPagination);
  const [page, setPage] = useState(1);
  const [activeTab, setActiveTab] = useState<HomeTabEnum>(HomeTabEnum.FOR_YOU);

  useEffect(() => {
    // Redirect to landing page if not authenticated
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        // For now, we'll fetch all reviews
        // TODO: Implement "For You" (popular) and "Following" (from followed users) logic in backend
        const filter = activeTab === HomeTabEnum.FOR_YOU ? FilterTypeEnum.LIKES : FilterTypeEnum.FOLLOWING;
        await dispatch(fetchReviewsAsync({ page, size: 20, filter })).unwrap();
      } catch (error) {
        console.error('Failed to fetch reviews:', error);
      }
    };

    if (isAuthenticated) {
      fetchReviews();
    }
  }, [isAuthenticated, page, activeTab, dispatch]);

  const handleTabChange = (tab: HomeTabEnum) => {
    setActiveTab(tab);
    setPage(1);
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
              For You
            </span>
            <span
              className={`tab ${activeTab === HomeTabEnum.FOLLOWING ? 'active' : ''}`}
              onClick={() => handleTabChange(HomeTabEnum.FOLLOWING)}
              style={{ cursor: 'pointer' }}
            >
              Following
            </span>
          </div>
        </div>

        {/* Contenido principal */}
        <section className="reviews-section">
          {loadingReviews ? (
            <div className="loading">Loading reviews...</div>
          ) : Object.values(reviews).length === 0 ? (
            <div className="empty-state">
              <h3>No reviews found</h3>
              <h4>Try the previous page or switch tabs</h4>
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
                    onClick={() => dispatch(fetchReviewsAsync({ page: pagination.page - 1, size: pagination.size, filter: FilterTypeEnum.RECENT }))}
                    className="btn btn-secondary"
                  >
                    Previous Page
                  </button>
                )}
                {pagination.totalCount > pagination.page * pagination.size && (
                  <button
                    onClick={() => dispatch(fetchReviewsAsync({ page: pagination.page + 1, size: pagination.size, filter: FilterTypeEnum.RECENT }))}
                    className="btn btn-primary"
                  >
                    Next Page
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

