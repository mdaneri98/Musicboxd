import Link from 'next/link';
import { imageRepository } from '@/repositories';
import { User } from '@/types';
import { useTranslation } from 'react-i18next';
import { ASSETS } from '@/utils';

interface UserCardProps {
  user: User;
}

const UserCard = ({ user }: UserCardProps) => {
  const { t } = useTranslation();
  const profileImageUrl = user.image_id
    ? imageRepository.getImageUrl(user.image_id)
    : ASSETS.DEFAULT_AVATAR;

  return (
    <Link href={`/users/${user.id}`} className="user-card">
      <div className="user-card-header">
        <img
          src={profileImageUrl}
          alt={user.username}
          className="user-card-image"
        />
        <div className="user-card-info">
          <h3 className="user-card-username">@{user.username}</h3>
          {user.name && <p className="user-card-name">{user.name}</p>}
        </div>
        {user.moderator && (
          <div className="user-card-badges">
            <span className="badge badge-moderator">{t('label.moderator')}</span>
          </div>
        )}
        {user.verified && (
          <div className="user-card-badges">
            <span className="badge badge-verified">{t('label.verified')}</span>
          </div>
        )}
      </div>

      <div className="user-card-stats">
        <div className="user-card-stat">
          <span className="user-card-stat-value">{user.review_amount}</span>
          <span className="user-card-stat-label">{t('label.reviews')}</span>
        </div>
        <div className="user-card-stat">
          <span className="user-card-stat-value">{user.followers_amount}</span>
          <span className="user-card-stat-label">{t('label.followers')}</span>
        </div>
        <div className="user-card-stat">
          <span className="user-card-stat-value">{user.following_amount}</span>
          <span className="user-card-stat-label">{t('label.following')}</span>
        </div>
      </div>
    </Link>
  );
};

export default UserCard;

