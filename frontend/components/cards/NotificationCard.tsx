/**
 * NotificationCard Component
 * Displays notification with type icon and mark as read functionality
 */

import Link from 'next/link';
import type { Notification } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import { markAsReadAsync } from '@/store/slices';
import { imageRepository } from '@/repositories';
import { NotificationTypeEnum } from '@/types';

interface NotificationCardProps {
  notification: Notification;
}

const NotificationCard = ({ notification }: NotificationCardProps) => {
  const dispatch = useAppDispatch();

  const handleMarkAsRead = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!notification.is_read) {
      await dispatch(markAsReadAsync(notification.id));
    }
  };

  // Get user image URL
  const triggerUserImageUrl = notification.trigger_user_image_id
    ? imageRepository.getImageUrl(notification.trigger_user_image_id)
    : '/default-avatar.png';

  // Get review item image URL (for LIKE, COMMENT, NEW_REVIEW)
  const reviewItemImageUrl = notification.review_item_image_id
    ? imageRepository.getImageUrl(notification.review_item_image_id)
    : null;

  const isReviewRelated = notification.type === NotificationTypeEnum.LIKE || notification.type === NotificationTypeEnum.COMMENT || notification.type === NotificationTypeEnum.NEW_REVIEW;

  return (
    <div className={`notification-item ${notification.is_read ? 'read' : 'unread'}`}>
      <div className="notification-content-wrapper">
        <div className="user-info">
          <Link href={`/users/${notification.trigger_user_id}`} className="user-details">
            <img
              src={triggerUserImageUrl}
              alt={notification.trigger_username}
              className="img-avatar"
            />
          </Link>

          <div className="notification-content">
            {isReviewRelated && notification.review_id ? (
              <Link href={`/reviews/${notification.review_id}`} className="notification-review-link">
                <div className="notification-text">
                  <p>{notification.message}</p>
                  <span className="notification-time">{notification.time_ago}</span>
                </div>
              </Link>
            ) : (
              <Link href={`/users/${notification.trigger_user_id}`}>
                <p>{notification.message}</p>
                <span className="notification-time">{notification.time_ago}</span>
              </Link>
            )}
          </div>
        </div>

        {isReviewRelated && reviewItemImageUrl && (
          <Link href={`/reviews/${notification.review_id}`}>
            <div className="notification-review-image">
              <img src={reviewItemImageUrl} alt={notification.review_item_name || ''} />
            </div>
          </Link>
        )}
      </div>

      {!notification.is_read && (
        <button onClick={handleMarkAsRead} className="mark-read-btn">
          Mark as read
        </button>
      )}
    </div>
  );
};

export default NotificationCard;
