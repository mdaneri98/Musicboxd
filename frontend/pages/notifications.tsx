import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { NotificationCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectOrderedNotifications, fetchNotificationsAsync, markAllAsReadAsync, selectNotificationPagination, selectUnreadCount, selectNotificationLoading } from '@/store/slices';
import { Notification } from '@/types';
import { LoadingSpinner } from '@/components/ui';

export default function NotificationsPage() {
  const { t } = useTranslation();
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
          <h2>{t('notifications.title')}</h2>
          {unreadCount > 0 && (
            <button onClick={handleMarkAllAsRead} className="mark-read-btn">
              {t('notifications.markAllRead')}
            </button>
          )}
        </div>

        {loadingNotifications ? (
          <>
            <div className="loading">{t('notifications.loading')}</div>
            <LoadingSpinner size="large" />
          </>
        ) : notifications.length === 0 ? (
          <p className="no-results">{t('notifications.noNotifications')}</p>
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
              {t('common.previous')} {t('notifications.page')}
            </button>
          )}
          {pagination.totalCount > pagination.page * pagination.size && (
            <button onClick={handleNextPage} className="btn btn-secondary">
              {t('common.next')} {t('notifications.page')}
            </button>
          )}
        </div>
      </div>
    </Layout>
  );
}

