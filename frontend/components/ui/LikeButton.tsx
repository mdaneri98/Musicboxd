import { useState } from 'react';
import { useAppDispatch } from '@/store/hooks';
import { likeReviewAsync, unlikeReviewAsync } from '@/store/slices';

/**
 * Like Button Component for Reviews
 * 
 * Best Practice: Encapsulates like/unlike logic with optimistic UI updates
 */

interface LikeButtonProps {
  reviewId: number;
  isLiked: boolean;
  likeCount: number;
  onLikeChange?: (isLiked: boolean, newCount: number) => void;
  className?: string;
}

export function LikeButton({
  reviewId,
  isLiked: initialIsLiked,
  likeCount: initialLikeCount,
  onLikeChange,
  className = '',
}: LikeButtonProps) {
  const dispatch = useAppDispatch();
  const [isLiked, setIsLiked] = useState(initialIsLiked);
  const [likeCount, setLikeCount] = useState(initialLikeCount);
  const [isLoading, setIsLoading] = useState(false);

  const handleToggleLike = async () => {
    setIsLoading(true);
    const previousLiked = isLiked;
    const previousCount = likeCount;

    try {
      // Optimistic update
      const newIsLiked = !isLiked;
      const newCount = newIsLiked ? likeCount + 1 : likeCount - 1;
      setIsLiked(newIsLiked);
      setLikeCount(newCount);

      if (isLiked) {
        await dispatch(unlikeReviewAsync(reviewId)).unwrap();
      } else {
        await dispatch(likeReviewAsync(reviewId)).unwrap();
      }

      onLikeChange?.(newIsLiked, newCount);
    } catch (error) {
      // Revert on error
      setIsLiked(previousLiked);
      setLikeCount(previousCount);
      console.error('Failed to toggle like:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onClick={handleToggleLike}
      disabled={isLoading}
      className={`like-button ${isLiked ? 'liked' : ''} ${className}`}
      type="button"
      aria-label={isLiked ? 'Unlike review' : 'Like review'}
    >
      <span className="like-icon">{isLiked ? '❤️' : '🤍'}</span>
      <span className="like-count">{likeCount}</span>
    </button>
  );
}

