import Link from 'next/link';
import { User } from '@/types';
import { imageRepository } from '@/repositories';

interface UserInfoProps {
  user: User;
  isOwnProfile: boolean;
  isAuthenticated: boolean;
  isFollowing: boolean;
  followLoading: boolean;
  onFollowToggle: () => void;
}

export const UserInfo: React.FC<UserInfoProps> = ({ user, isOwnProfile, isAuthenticated, isFollowing, followLoading, onFollowToggle }) => {
  const userImgUrl = user.image_id ? imageRepository.getImageUrl(user.image_id) : '/assets/default-user.png';
  
  return (
    <section className="user-profile-header">
      <div className="user-profile-main">
        <Link href={`/users/${user.id}`} className="user-profile-image-link">
          <img src={userImgUrl} alt={user.username} className="user-profile-image" />
        </Link>
        <div className="user-profile-info">
          <div className="entity-type">User</div>
          <div className="user-badges">
            {user.verified && (
              <span className="badge badge-verified">Verified</span>
            )}
            {user.moderator && (
              <span className="badge badge-moderator">Moderator</span>
            )}
          </div>
          <Link href={`/users/${user.id}`} className="user-profile-name">
            <h1>@{user.username}</h1>
            <div className="user-name-badges">
              {user.name && <h3>{user.name}</h3>}
            </div>
          </Link>
          {user.bio && <p className="user-profile-bio">{user.bio}</p>}

          <div className="user-profile-stats">
            <Link href={`/users/${user.id}?tab=reviews`} className="stat-link">
              <span className="stat-value">{user.review_amount || 0}</span>
              <span className="stat-label">Reviews</span>
            </Link>

            <Link href={`/users/${user.id}/followers`} className="stat-link">
              <span className="stat-value">{user.followers_amount || 0}</span>
              <span className="stat-label">Followers</span>
            </Link>

            <Link href={`/users/${user.id}/following`} className="stat-link">
              <span className="stat-value">{user.following_amount || 0}</span>
              <span className="stat-label">Following</span>
            </Link>
          </div>
        </div>
      </div>
      {isOwnProfile ? (
        <div className="user-profile-actions">
          <Link href="/profile/edit" className="btn btn-primary">
            Edit Profile
          </Link>
        </div>
      ) : (
        <div className="user-profile-actions"> 
          <button className={`btn ${isFollowing ? 'btn-secondary' : 'btn-primary'}`} onClick={onFollowToggle} disabled={followLoading}>
            {followLoading ? 'Loading...' : (isFollowing ? 'Unfollow' : 'Follow')}
          </button>
        </div>
      )
      }

    </section>
  );
};

