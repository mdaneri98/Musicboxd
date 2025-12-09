import { useState } from 'react';
import { useAppDispatch } from '@/store/hooks';
import { followUserAsync, unfollowUserAsync } from '@/store/slices';
import { useTranslation } from 'react-i18next';
/**
 * Follow/Unfollow Button Component
 * 
 * Best Practice: Encapsulates follow/unfollow logic with optimistic UI updates
 */

interface FollowButtonProps {
  userId: number;
  isFollowing: boolean;
  onFollowChange?: (isFollowing: boolean) => void;
  className?: string;
}

export function FollowButton({
  userId,
  isFollowing: initialIsFollowing,
  onFollowChange,
  className = '',
}: FollowButtonProps) {
  const dispatch = useAppDispatch();
  const [isFollowing, setIsFollowing] = useState(initialIsFollowing);
  const [isLoading, setIsLoading] = useState(false);
  const { t } = useTranslation();
  const handleToggleFollow = async () => {
    setIsLoading(true);
    const previousState = isFollowing;

    try {
      setIsFollowing(!isFollowing);

      if (isFollowing) {
        await dispatch(unfollowUserAsync(userId)).unwrap();
        onFollowChange?.(false);
      } else {
        await dispatch(followUserAsync(userId)).unwrap();
        onFollowChange?.(true);
      }
    } catch (error) {
      // Revert on error
      setIsFollowing(previousState);
      console.error(t("common.failedToToggleFollow"), error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onClick={handleToggleFollow}
      disabled={isLoading}
      className={`btn ${isFollowing ? 'btn-secondary' : 'btn-primary'} ${className}`}
      type="button"
    >
      {isLoading ? t("common.loading") : isFollowing ? t("common.unfollow") : t("common.follow")}
    </button>
  );
}

