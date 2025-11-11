/**
 * ReviewCard Component
 * Displays review information with rating, likes, and comments
 * Migrated from: components/review_card.jsp
 */

import Link from 'next/link';
import { Review, ReviewItemTypeEnum } from '@/types';
import { imageRepository } from '@/repositories';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsModerator } from '@/store/slices';
import {
  likeReviewAsync,
  unlikeReviewAsync,
  blockReviewAsync,
  unblockReviewAsync,
} from '@/store/slices';

interface ReviewCardProps {
  review: Review;
}

const ReviewCard = ({ review }: ReviewCardProps) => {
  const dispatch = useAppDispatch();
  const isModerator = useAppSelector(selectIsModerator);

  const handleLike = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (review.is_liked) {
      await dispatch(unlikeReviewAsync(review.id));
    } else {
      await dispatch(likeReviewAsync(review.id));
    }
  };

  const handleBlock = async (e: React.MouseEvent) => {
    e.preventDefault();
    if (review.is_blocked) {
      await dispatch(unblockReviewAsync(review.id));
    } else {
      await dispatch(blockReviewAsync(review.id));
    }
  };

  const itemImageUrl = review.item_id
    ? imageRepository.getImageUrl(review.item_id)
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

  const getItemTypeLabel = () => {
    return review.item_type;
  };

  if (review.is_blocked) {
    return (
      <div className="review-card">
        <div className="review-content blocked">
          <h4 className="review-content-title">
            This review was blocked by a moderator
          </h4>
          <p className="review-description">Try making another one</p>
          {isModerator && (
            <button
              onClick={handleBlock}
              className="btn btn-secondary"
            >
              Unblock
              <i className="fa-solid fa-ban"></i>
            </button>
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
          <p className="review-type">{getItemTypeLabel()}</p>
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
                {new Date(review.created_at).toLocaleDateString()}
              </span>
              <span className="user-name">{review.username}</span>
            </div>
          </Link>
        </div>

        <div className="review-actions">
          {/* Like action */}
          <div className="action-item">
            <Link href={`/reviews/${review.id}?page=likes`}>
              <span className="action-count">{review.likes}</span>
            </Link>
            <button onClick={handleLike} className={`action-link ${review.is_liked ? 'active' : ''}`}>
              <i className={`fa-${review.is_liked ? 'solid' : 'regular'} fa-heart`}></i>
            </button>
          </div>

          {/* Comment action */}
          <div className="action-item">
            <Link href={`/reviews/${review.id}`} className="action-link">
              <span className="action-count">{review.comment_amount}</span>
              <i className="fa-regular fa-comment"></i>
            </Link>
          </div>

          {/* Moderator actions */}
          {isModerator && (
            <div className="action-item">
              <button onClick={handleBlock} className="action-link danger">
                <i className="fa-solid fa-ban"></i>
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ReviewCard;

