/**
 * NotificationCard Component
 * Displays notification with type icon and mark as read functionality
 */

import Link from 'next/link';
import { Notification } from '@/types';
import { useAppDispatch } from '@/store/hooks';
import { markAsReadAsync, deleteNotificationAsync } from '@/store/slices';

interface NotificationCardProps {
  notification: Notification;
}

const NotificationCard = ({ notification }: NotificationCardProps) => {
  const dispatch = useAppDispatch();

  const handleMarkAsRead = async () => {
    if (!notification.is_read) {
      await dispatch(markAsReadAsync(notification.id));
    }
  };

  const handleDelete = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    await dispatch(deleteNotificationAsync(notification.id));
  };

  const getNotificationIcon = () => {
    switch (notification.type) {
      case 'FOLLOW':
        return 'fa-user-plus';
      case 'LIKE':
        return 'fa-heart';
      case 'COMMENT':
        return 'fa-comment';
      case 'REVIEW':
        return 'fa-star';
      default:
        return 'fa-bell';
    }
  };

  const getNotificationLink = () => {
    if (notification.review_id) {
      switch (notification.type) {
        case 'FOLLOW':
          return `/users/${notification.trigger_user_id}`;
        case 'LIKE':
        case 'COMMENT':
        case 'REVIEW':
          return `/reviews/${notification.review_id}`;
        default:
          return '/notifications';
      }
    }
    return '/notifications';
  };

  return (
    <Link
      href={getNotificationLink()}
      className={`notification-card ${notification.is_read ? 'read' : 'unread'}`}
      onClick={handleMarkAsRead}
    >
      <div className="notification-icon">
        <i className={`fas ${getNotificationIcon()}`}></i>
      </div>
      <div className="notification-content">
        <p className="notification-message">{notification.message}</p>
        <span className="notification-timestamp">
          {notification.time_ago}
        </span>
      </div>
      {!notification.is_read && <div className="notification-indicator"></div>}
      <button
        onClick={handleDelete}
        className="notification-delete"
        title="Delete notification"
      >
        <i className="fas fa-times"></i>
      </button>
    </Link>
  );
};

export default NotificationCard;

