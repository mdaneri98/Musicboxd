/**
 * ReviewCard Component
 * Displays review information with rating, likes, and comments
 */

import Link from 'next/link';
import { Review, ReviewItemTypeEnum } from '@/types';
import { imageRepository } from '@/repositories';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectCurrentUser, selectIsModerator } from '@/store/slices';
import { useTranslation } from 'react-i18next';
import {
  likeReviewAsync,
  unlikeReviewAsync,
  blockReviewAsync,
  unblockReviewAsync,
} from '@/store/slices';
import { formatTimeAgo } from '@/utils/timeUtils';
import { ConfirmationModal } from '../ui';
import { useState } from 'react';

interface ReviewCardProps {
  review: Review;
}

const ReviewCard = ({ review }: ReviewCardProps) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const isModerator = useAppSelector(selectIsModerator);
  const currentUser = useAppSelector(selectCurrentUser);
  const [reviewToBlock, setReviewToBlock] = useState(false);
  const [isBlocked, setIsBlocked] = useState(review.is_blocked);

  const handleLike = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (review.liked) {
      await dispatch(unlikeReviewAsync(review.id));
    } else {
      await dispatch(likeReviewAsync(review.id));
    }
  };

  const handleBlock = async (reviewId: number) => {
    if (isBlocked) {
      await dispatch(unblockReviewAsync(reviewId));
      setIsBlocked(false);
    } else {
      await dispatch(blockReviewAsync(reviewId));
      setIsBlocked(true);
    }
    setReviewToBlock(false);
  };

  const itemImageUrl = review.item_image_id
    ? imageRepository.getImageUrl(review.item_image_id)
    : '/assets/default-album.png';

  const userImageUrl = review.user_image_id
    ? imageRepository.getImageUrl(review.user_image_id)
    : '/assets/default-avatar.png';

  const getItemUrl = () => {
    switch (review.item_type) {
      case ReviewItemTypeEnum.ARTIST:
        return `/artists/${review.item_id}`;
      case ReviewItemTypeEnum.ALBUM:
        return `/albums/${review.item_id}`;
      case ReviewItemTypeEnum.SONG:
        return `/songs/${review.item_id}`;
      default:
        return '/';
    }
  };

  const getItemTypeTranslationKey = () => {
    switch (review.item_type) {
      case ReviewItemTypeEnum.ARTIST:
        return 'review.itemType.artist';
      case ReviewItemTypeEnum.ALBUM:
        return 'review.itemType.album';
      case ReviewItemTypeEnum.SONG:
        return 'review.itemType.song';
      default:
        return 'review.itemType.unknown';
    }
  };

  if (isBlocked) {
    return (
      <div className="review-card">
        <div className="review-content blocked">
          <h4 className="review-content-title">
            {t('review.blockedByModerator')}
          </h4>
          {review.user_id == currentUser?.id && (
          <p className="review-description">{t('review.tryMakingAnother')}</p>
           )}
          {isModerator && (
            <div>
              <div className="review-content">
                <h4 className="review-content-title">{review.title}</h4>
                <p className="review-description">{review.description}</p>
              </div>
              <button
                onClick={() => handleBlock(review.id)}
                className="btn btn-secondary"
              >
                {t('review.unblock') + ' '} 
                <i className="fa-solid fa-ban"></i>
              </button>
            </div>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="review-card">
      <Link href={getItemUrl()} className="review-header">
        <div className="review-image">
          <img
            src={itemImageUrl}
            alt={review.item_name}
            className="img-cover"
          />
        </div>
        <div className="review-header-info">
          <h3 className="review-title">{review.item_name}</h3>
          <p className="review-type">{t(getItemTypeTranslationKey())}</p>
        </div>
        <div className="rating-display">
          <div className="star-rating">
            {[1, 2, 3, 4, 5].map((star) => (
              <span
                key={star}
                className={`star ${star <= review.rating ? 'filled' : ''}`}
              >
                &#9733;
              </span>
            ))}
          </div>
        </div>
      </Link>

      <div className="review-content">
        <h4 className="review-content-title">{review.title}</h4>
        <p className="review-description">{review.description}</p>
      </div>

      <div className="review-footer">
        <div className="user-info">
          <Link href={`/users/${review.user_id}`} className="user-link">
            <img
              src={userImageUrl}
              alt={review.username}
              className="img-avatar"
            />
            <div className="user-details">
              <span className="review-timestamp">
                {formatTimeAgo(review.created_at)}
              </span>
              <span className="user-name">{review.username}</span>
              <div className="user-badges">
                {review.user_verified && (
                  <span className="badge badge-verified">{t("label.verified")}</span>
                )}
                {review.user_moderator && (
                  <span className="badge badge-moderator">{t("label.moderator")}</span>
                )}
              </div>
            </div>
          </Link>
        </div>

        <div className="review-actions">
          {/* Like action */}
          <div className="action-item">
            <Link href={`/reviews/${review.id}?tab=likes`}>
              <span className="action-count">{review.likes}</span>
            </Link>
            <button onClick={handleLike} className={`action-btn ${review.liked ? 'active' : ''}`}>
              <i className={`fa-${review.liked ? 'solid' : 'regular'} fa-heart`}></i>
            </button>
          </div>

          {/* Comment action */}
          <div className="action-item">
            <Link href={`/reviews/${review.id}`} className="action-link">
              <span className="action-count"> {review.comment_amount} </span>
              <i className="fa-regular fa-comment"></i>
            </Link>
          </div>

          {/* Moderator actions */}
          {isModerator && (
            <div className="action-item">
              <button onClick={() => setReviewToBlock(true)} className="action-btn danger">
                <i className="fa-solid fa-ban"></i>
              </button>
            </div>
          )}
        </div>
      </div>
      <ConfirmationModal
        isOpen={reviewToBlock}
        message={t('review.confirmBlockReview')}
        onConfirm={() => handleBlock(review.id)}
        onCancel={() => setReviewToBlock(false)}
        confirmText={t('review.block')}
        cancelText={t('review.cancel')}
      />
    </div>
  );


};

export default ReviewCard;

