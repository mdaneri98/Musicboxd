/**
 * Home Page (Authenticated)
 * Main feed page for authenticated users with For You / Following tabs
 * Migrated from: home.jsp
 */

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated } from '@/store/slices';
import { reviewRepository } from '@/repositories';
import { Review, FilterTypeEnum } from '@/types';

const HomePage = () => {
  const router = useRouter();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [activeTab, setActiveTab] = useState<'forYou' | 'following'>('forYou');

  useEffect(() => {
    // Redirect to landing page if not authenticated
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        setLoading(true);
        // For now, we'll fetch all reviews
        // TODO: Implement "For You" (popular) and "Following" (from followed users) logic in backend
        const filter = activeTab === 'forYou' ? FilterTypeEnum.LIKES : FilterTypeEnum.RECENT;
        const response = await reviewRepository.getReviews(page, 20, undefined, filter);
        const reviews = response.items.map((item) => item.data);
        setReviews(reviews);
        setHasMore(reviews.length === 20);
      } catch (error) {
        console.error('Failed to fetch reviews:', error);
      } finally {
        setLoading(false);
      }
    };

    if (isAuthenticated) {
      fetchReviews();
    }
  }, [isAuthenticated, page, activeTab]);

  const handleTabChange = (tab: 'forYou' | 'following') => {
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
              className={`tab ${activeTab === 'forYou' ? 'active' : ''}`}
              onClick={() => handleTabChange('forYou')}
              style={{ cursor: 'pointer' }}
            >
              For You
            </span>
            <span
              className={`tab ${activeTab === 'following' ? 'active' : ''}`}
              onClick={() => handleTabChange('following')}
              style={{ cursor: 'pointer' }}
            >
              Following
            </span>
          </div>
        </div>

        {/* Contenido principal */}
        <section className="reviews-section">
          {loading ? (
            <div className="loading">Loading reviews...</div>
          ) : reviews.length === 0 ? (
            <div className="empty-state">
              <h3>No reviews found</h3>
              <h4>Try the previous page or switch tabs</h4>
            </div>
          ) : (
            <>
              <div className="reviews-grid">
                {reviews.map((review) => (
                  <ReviewCard key={review.id} review={review} />
                ))}
              </div>

              {/* Paginación */}
              <div className="pagination">
                {page > 1 && (
                  <button
                    onClick={() => setPage(page - 1)}
                    className="btn btn-secondary"
                  >
                    Previous Page
                  </button>
                )}
                {hasMore && (
                  <button
                    onClick={() => setPage(page + 1)}
                    className="btn btn-secondary"
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

