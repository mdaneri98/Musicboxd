import { useState } from 'react';
import { useAppDispatch } from '@/store/hooks';
import { followUserAsync, unfollowUserAsync } from '@/store/slices';

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

  const handleToggleFollow = async () => {
    setIsLoading(true);
    const previousState = isFollowing;

    try {
      // Optimistic update
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
      console.error('Failed to toggle follow:', error);
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
      {isLoading ? 'Loading...' : isFollowing ? 'Unfollow' : 'Follow'}
    </button>
  );
}

