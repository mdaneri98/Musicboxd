import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { NotificationCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, fetchNotificationsAsync, markAllAsReadAsync, selectNotificationPagination, selectNotifications, selectUnreadCount, selectNotificationLoading } from '@/store/slices';

export default function NotificationsPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const notifications = useAppSelector(selectNotifications);
  const unreadCount = useAppSelector(selectUnreadCount);
  const loadingNotifications = useAppSelector(selectNotificationLoading);
  const pagination = useAppSelector(selectNotificationPagination);

  // Redirect to landing if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  // Fetch notifications
  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        await dispatch(fetchNotificationsAsync({ page: pagination.page, size: pagination.size })).unwrap();
      } catch (error) {
        console.error('Failed to fetch notifications:', error);
      }
    };

    if (isAuthenticated) {
      fetchNotifications();
    }
  }, [isAuthenticated, pagination, dispatch]);

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
  const handlePreviousPage = () => {
    dispatch(fetchNotificationsAsync({ page: pagination.page - 1, size: pagination.size })).unwrap();
  };

  const handleNextPage = () => {
    dispatch(fetchNotificationsAsync({ page: pagination.page + 1, size: pagination.size })).unwrap();
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
        ) : Object.values(notifications).length === 0 ? (
          <p className="no-results">No notifications</p>
        ) : (
          <div className="notifications-list">
            {Object.values(notifications).map((notification) => (
              <NotificationCard
                key={notification.id}
                notification={notification}
              />
            ))}
          </div>
        )}

        {/* Pagination */}
        {pagination.totalCount > pagination.page * pagination.size && (
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
        )}
      </div>
    </Layout>
  );
}

