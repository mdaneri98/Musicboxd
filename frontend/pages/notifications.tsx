import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { NotificationCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectOrderedNotifications, fetchNotificationsAsync, markAllAsReadAsync, selectNotificationPagination, selectUnreadCount, selectNotificationLoading } from '@/store/slices';
import { Notification } from '@/types';

export default function NotificationsPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const notifications = useAppSelector(selectOrderedNotifications);
  const unreadCount = useAppSelector(selectUnreadCount);
  const loadingNotifications = useAppSelector(selectNotificationLoading);
  const pagination = useAppSelector(selectNotificationPagination);

  // Redirect to landing if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  // Fetch notifications only on mount
  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        await dispatch(fetchNotificationsAsync({ page: 1, size: 20 })).unwrap();
      } catch (error) {
        console.error('Failed to fetch notifications:', error);
      }
    };

    if (isAuthenticated) {
      fetchNotifications();
    }
  }, [isAuthenticated, dispatch]);

  // Mark all as read
  const handleMarkAllAsRead = async () => {
    try {
      await dispatch(markAllAsReadAsync()).unwrap();
      
      // Refresh notifications
      await dispatch(fetchNotificationsAsync({ page: pagination.page, size: pagination.size })).unwrap();
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  };


  // Handle page navigation
  const handlePreviousPage = async () => {
    try {
      await dispatch(fetchNotificationsAsync({ page: pagination.page - 1, size: pagination.size })).unwrap();
    } catch (error) {
      console.error('Failed to fetch previous page:', error);
    }
  };

  const handleNextPage = async () => {
    try {
      await dispatch(fetchNotificationsAsync({ page: pagination.page + 1, size: pagination.size })).unwrap();
    } catch (error) {
      console.error('Failed to fetch next page:', error);
    }
  };

  if (!isAuthenticated) {
    return null; // Will redirect in useEffect
  }

  return (
    <Layout title="Notifications - Musicboxd">
      <div className="notifications-container">
        <div className="notifications">
          <h2>Notifications</h2>
          {unreadCount > 0 && (
            <button onClick={handleMarkAllAsRead} className="mark-read-btn">
              Mark all as read
            </button>
          )}
        </div>

        {loadingNotifications ? (
          <div className="loading">Loading notifications...</div>
        ) : notifications.length === 0 ? (
          <p className="no-results">No notifications</p>
        ) : (
          <div className="notifications-list">
            {notifications.map((notification: Notification) => (
              <NotificationCard
                key={notification.id}
                notification={notification}
              />
            ))}
          </div>
        )}

        {/* Pagination */}
        <div className="pagination">
          {pagination.page > 1 && (
            <button onClick={handlePreviousPage} className="btn btn-secondary">
              Previous Page
            </button>
          )}
          {pagination.totalCount > pagination.page * pagination.size && (
            <button onClick={handleNextPage} className="btn btn-secondary">
              Next Page
            </button>
          )}
        </div>
      </div>
    </Layout>
  );
}

