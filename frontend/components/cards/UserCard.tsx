/**
 * UserCard Component
 * Displays user information in a card format
 * Migrated from: components/user_card.jsp
 */

import Link from 'next/link';
import { User } from '@/types';

interface UserCardProps {
  user: User;
}

const UserCard = ({ user }: UserCardProps) => {
  const profileImageUrl = user.imageId
    ? `/api/images/${user.imageId}`
    : '/assets/default-avatar.png';

  return (
    <Link href={`/users/${user.id}`} className="user-card">
      <div className="user-card-header">
        <img
          src={profileImageUrl}
          alt={user.username}
          className="user-card-image"
        />
        <div className="user-card-info">
          <h3 className="user-card-username">{user.username}</h3>
          {user.bio && <p className="user-card-name">{user.bio}</p>}
        </div>
        {user.isModerator && (
          <div className="user-card-badges">
            <span className="badge badge-moderator">Moderator</span>
          </div>
        )}
      </div>

      {user.bio && <p className="user-card-bio">{user.bio}</p>}

      <div className="user-card-stats">
        <div className="user-card-stat">
          <span className="user-card-stat-value">{user.reviewsAmount}</span>
          <span className="user-card-stat-label">Reviews</span>
        </div>
        <div className="user-card-stat">
          <span className="user-card-stat-value">{user.followersAmount}</span>
          <span className="user-card-stat-label">Followers</span>
        </div>
        <div className="user-card-stat">
          <span className="user-card-stat-value">{user.followingAmount}</span>
          <span className="user-card-stat-label">Following</span>
        </div>
      </div>
    </Link>
  );
};

export default UserCard;

