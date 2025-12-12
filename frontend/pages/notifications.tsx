import { useEffect, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { NotificationCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import { 
  selectIsAuthenticated, 
  selectOrderedNotifications, 
  fetchNotificationsAsync, 
  fetchMoreNotificationsAsync,
  markAllAsReadAsync, 
  selectNotificationPagination, 
  selectUnreadCount, 
  selectNotificationLoading,
  selectNotificationLoadingMore,
  selectNotificationsHasMore,
} from '@/store/slices';
import { Notification } from '@/types';

export default function NotificationsPage() {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const notifications = useAppSelector(selectOrderedNotifications);
  const unreadCount = useAppSelector(selectUnreadCount);
  const loading = useAppSelector(selectNotificationLoading);
  const loadingMore = useAppSelector(selectNotificationLoadingMore);
  const pagination = useAppSelector(selectNotificationPagination);
  const hasMore = useAppSelector(selectNotificationsHasMore);

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
        await dispatch(fetchNotificationsAsync({ page: 1, size: 10 })).unwrap();
      } catch (error) { 
        console.error('Failed to fetch notifications:', error);
      }
    };

    if (isAuthenticated) {
      fetchNotifications();
    }
  }, [isAuthenticated, dispatch]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!hasMore || loadingMore) return;
    
    const nextPage = pagination.page + 1;
    await dispatch(fetchMoreNotificationsAsync({ 
      page: nextPage, 
      size: pagination.size 
    }));
  }, [dispatch, pagination.page, pagination.size, hasMore, loadingMore]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore,
    isLoading: loading || loadingMore,
    enabled: isAuthenticated && !loading,
  });

  // Mark all as read
  const handleMarkAllAsRead = async () => {
    try {
      await dispatch(markAllAsReadAsync()).unwrap();
      // Refresh notifications
      await dispatch(fetchNotificationsAsync({ page: 1, size: pagination.size })).unwrap();
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  };

  if (!isAuthenticated) {
    return null;
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

        {loading && notifications.length === 0 ? (
          <LoadingSpinner size="large" message={t('notifications.loading')} />
        ) : notifications.length === 0 ? (
          <p className="no-results">{t('notifications.noNotifications')}</p>
        ) : (
          <>
            <div className="notifications-list">
              {notifications.map((notification: Notification) => (
                <NotificationCard
                  key={notification.id}
                  notification={notification}
                />
              ))}
            </div>

            {/* Sentinel element for infinite scroll */}
            <div ref={sentinelRef} className="infinite-scroll-sentinel" />

            {/* Loading indicator for more content */}
            {(loadingMore || isFetchingMore) && (
              <div className="loading-more">
                <LoadingSpinner size="small" />
              </div>
            )}

            {/* End of content message */}
            {!hasMore && notifications.length > 0 && (
              <div className="end-of-content">
                <p>{t('common.noMoreContent')}</p>
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
}
