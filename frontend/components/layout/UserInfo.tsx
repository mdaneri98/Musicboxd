import Link from 'next/link';
import { User } from '@/types';
import { imageRepository } from '@/repositories';

interface UserInfoProps {
  user: User;
}

export const UserInfo: React.FC<UserInfoProps> = ({ user }) => {
  const userImgUrl = user.imageId ? imageRepository.getImageUrl(user.imageId) : '/assets/default-user.png';
  
  return (
    <section className="user-profile-header">
      <div className="user-profile-main">
        <Link href={`/users/${user.id}`} className="user-profile-image-link">
          <img src={userImgUrl} alt={user.username} className="user-profile-image" />
        </Link>
        <div className="user-profile-info">
          <div className="entity-type">User</div>
          <div className="user-badges">
            {user.isVerified && (
              <span className="badge badge-verified">Verified</span>
            )}
            {user.isModerator && (
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
              <span className="stat-value">{user.reviewsAmount || 0}</span>
              <span className="stat-label">Reviews</span>
            </Link>

            <Link href={`/users/${user.id}/followers`} className="stat-link">
              <span className="stat-value">{user.followersAmount || 0}</span>
              <span className="stat-label">Followers</span>
            </Link>

            <Link href={`/users/${user.id}/following`} className="stat-link">
              <span className="stat-value">{user.followingAmount || 0}</span>
              <span className="stat-label">Following</span>
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
};

