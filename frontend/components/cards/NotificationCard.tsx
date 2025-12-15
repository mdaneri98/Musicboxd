/**
 * NotificationCard Component
 * Displays notification with type icon and mark as read functionality
 */

import Link from 'next/link';
import type { Notification } from '@/types';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { markAsReadAsync, selectCurrentUser } from '@/store/slices';
import { imageRepository } from '@/repositories';
import { NotificationTypeEnum } from '@/types';
import { useTranslation } from 'react-i18next';
import { formatTimeAgo } from '@/utils/timeUtils';
import { ASSETS } from '@/utils';

interface NotificationCardProps {
  notification: Notification;
}

const NotificationCard = ({ notification }: NotificationCardProps) => {
  const dispatch = useAppDispatch();
  const { t } = useTranslation();
  const currentUser = useAppSelector(selectCurrentUser);
  
  const handleMarkAsRead = () => {
    if (!notification.is_read) {
      dispatch(markAsReadAsync(notification.id));
    }
  };

  // Get user image URL
  const triggerUserImageUrl = notification.trigger_user_image_id
    ? imageRepository.getImageUrl(notification.trigger_user_image_id)
    : ASSETS.DEFAULT_AVATAR;

  const currentUserImageUrl = currentUser?.image_id
    ? imageRepository.getImageUrl(currentUser?.image_id)
    : ASSETS.DEFAULT_AVATAR;

  // Get review item image URL (for LIKE, COMMENT, NEW_REVIEW)
  const reviewItemImageUrl = notification.review_item_image_id
    ? imageRepository.getImageUrl(notification.review_item_image_id)
    : null;

  const isReviewRelated = notification.type === NotificationTypeEnum.LIKE || notification.type === NotificationTypeEnum.COMMENT || notification.type === NotificationTypeEnum.NEW_REVIEW || notification.type === NotificationTypeEnum.REVIEW_BLOCKED || notification.type === NotificationTypeEnum.REVIEW_UNBLOCKED;
  const isReviewBlockedRelated = notification.type === NotificationTypeEnum.REVIEW_BLOCKED || notification.type === NotificationTypeEnum.REVIEW_UNBLOCKED;

  return (
    <div className={`notification-item ${notification.is_read ? 'read' : 'unread'}`}>
      <div className="notification-content-wrapper">
        <div className="user-info">
          <Link 
            href={isReviewBlockedRelated ? `/users/${currentUser?.id}` : `/users/${notification.trigger_user_id}`} 
            className="user-details"
            onClick={handleMarkAsRead}
          >
            <img
              src={isReviewBlockedRelated ? currentUserImageUrl : triggerUserImageUrl}
              alt={notification.trigger_username}
              className="img-avatar"
            />
          </Link>

          <div className="notification-content">
            <Link 
              href={isReviewRelated ? `/reviews/${notification.review_id}` : `/users/${notification.trigger_user_id}`}
              onClick={handleMarkAsRead}
            >
              <div className="notification-text">
                <p>{t(`notifications.types.${notification.type}`, { username: notification.trigger_username, itemName: notification.review_item_name })}</p>
                <span className="notification-time">{formatTimeAgo(notification.created_at)}</span>
              </div>
            </Link>
          </div>
        </div>

        {isReviewRelated && reviewItemImageUrl && (
          <Link href={`/reviews/${notification.review_id}`} onClick={handleMarkAsRead}>
            <div className="notification-review-image">
              <img src={reviewItemImageUrl} alt={notification.review_item_name || ''} />
            </div>
          </Link>
        )}
      </div>

    </div>
  );
};

export default NotificationCard;
