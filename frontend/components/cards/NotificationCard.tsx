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
    if (!notification.isRead) {
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
      case 'follow':
        return 'fa-user-plus';
      case 'like':
        return 'fa-heart';
      case 'comment':
        return 'fa-comment';
      case 'review':
        return 'fa-star';
      default:
        return 'fa-bell';
    }
  };

  const getNotificationLink = () => {
    if (notification.relatedId) {
      switch (notification.type) {
        case 'follow':
          return `/users/${notification.relatedId}`;
        case 'like':
        case 'comment':
        case 'review':
          return `/reviews/${notification.relatedId}`;
        default:
          return '/notifications';
      }
    }
    return '/notifications';
  };

  return (
    <Link
      href={getNotificationLink()}
      className={`notification-card ${notification.isRead ? 'read' : 'unread'}`}
      onClick={handleMarkAsRead}
    >
      <div className="notification-icon">
        <i className={`fas ${getNotificationIcon()}`}></i>
      </div>
      <div className="notification-content">
        <p className="notification-message">{notification.message}</p>
        <span className="notification-timestamp">
          {new Date(notification.createdAt).toLocaleDateString()}
        </span>
      </div>
      {!notification.isRead && <div className="notification-indicator"></div>}
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

