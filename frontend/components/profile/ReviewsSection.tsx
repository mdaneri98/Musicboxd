import { RefObject } from 'react';
import { useTranslation } from 'react-i18next';
import { ReviewCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import type { Review } from '@/types';

interface ReviewsSectionProps {
  reviews: Review[];
  loading?: boolean;
  loadingMore?: boolean;
  isFetchingMore?: boolean;
  hasMore?: boolean;
  sentinelRef?: RefObject<HTMLDivElement | null>;
}

export default function ReviewsSection({
  reviews,
  loading,
  loadingMore,
  isFetchingMore,
  hasMore,
  sentinelRef,
}: ReviewsSectionProps) {
  const { t } = useTranslation();

  return (
    <section className="reviews-section">
      {loading && reviews.length === 0 ? (
        <LoadingSpinner size="large" />
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
          {sentinelRef && <div ref={sentinelRef} className="infinite-scroll-sentinel" />}

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
  );
}
