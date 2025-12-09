/**
 * RatingCard Component
 * Displays rating statistics and action button
 * Migrated from: components/rating_card.jsp
 */

import Link from 'next/link';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated } from '@/store/slices';
import { useTranslation } from 'react-i18next';

interface RatingCardProps {
  totalRatings: number;
  averageRating: number;
  userRating?: number;
  reviewed: boolean;
  entityType: 'artists' | 'albums' | 'songs';
  entityId: number;
  entityLabel: string;
}

const RatingCard = ({
  totalRatings,
  averageRating,
  userRating,
  reviewed,
  entityType,
  entityId,
  entityLabel,
}: RatingCardProps) => {
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const { t } = useTranslation();
  return (
    <div className="rating-card">
      <div className="rating-stats">
        <div className="rating-stat-item">
          <div className="rating-value">{totalRatings}</div>
          <div className="rating-label">{t("common.totalRatings")}</div>
        </div>
        <div className="rating-stat-item">
          <div className="rating-value">
            <span className="star filled">&#9733;</span>
            {averageRating.toFixed(2)}
            <span className="rating-max">/ 5</span>
          </div>
          <div className="rating-label">{t("common.averageRating")}</div>
        </div>
        <div className="rating-stat-item">
          <div className="rating-value">
            {reviewed ? (
              <>
                <span className="star filled">&#9733;</span>
                {userRating}
              </>
            ) : (
              <span className="star">&#9733;</span>
            )}
            <span className="rating-max">/ 5</span>
          </div>
          <div className="rating-label">{t("common.yourRating")}</div>
        </div>
      </div>

      <div className="rating-actions">
        {!isAuthenticated ? (
          <Link href="/login" className="btn btn-primary btn-block">
            {t("common.loginToReview")}
            <i className="fas fa-star"></i>
          </Link>
        ) : !reviewed ? (
          <Link
            href={`/${entityType}/${entityId}/reviews`}
            className="btn btn-primary btn-block"
          >
            <i className="fas fa-star"></i>
            {t(`common.rateThis.${entityLabel}`)}
          </Link>
        ) : (
          <Link
            href={`/${entityType}/${entityId}/reviews`}
            className="btn btn-secondary btn-block"
          >
            <i className="fas fa-edit"></i>
            {t("common.editYourReview")}
          </Link>
        )}
      </div>
    </div>
  );
};

export default RatingCard;

