import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { NotificationCard } from '@/components/cards';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated } from '@/store/slices';
import { notificationRepository } from '@/repositories';
import { HALResource, Notification } from '@/types';

export default function NotificationsPage() {
  const router = useRouter();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const { pageNum = '0' } = router.query;
  
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [showPrevious, setShowPrevious] = useState(false);
  const [showNext, setShowNext] = useState(false);
  
  const page = parseInt(pageNum as string);
  const pageSize = 20;

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
        setLoading(true);
        const [notificationsData] = await Promise.all([
          notificationRepository.getNotifications(page, pageSize),
          // notificationRepository.getUnreadCount(),
        ]);

        setNotifications(notificationsData.items.map((item: HALResource<Notification>) => item.data as Notification));
        // setUnreadCount(unreadCountData);
        
        // Pagination
        setShowPrevious(page > 1);
        setShowNext(notificationsData.items.length === pageSize);
      } catch (error) {
        console.error('Failed to fetch notifications:', error);
      } finally {
        setLoading(false);
      }
    };

    if (isAuthenticated) {
      fetchNotifications();
    }
  }, [isAuthenticated, page]);

  // Mark all as read
  const handleMarkAllAsRead = async () => {
    try {
      await notificationRepository.markAllAsRead();
      
      // Refresh notifications
      const notificationsData = await notificationRepository.getNotifications(page, pageSize);
      setNotifications(notificationsData.items.map((item: HALResource<Notification>) => item.data as Notification));
      setUnreadCount(0);
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  };


  // Handle page navigation
  const handlePreviousPage = () => {
    router.push(`/notifications?pageNum=${page - 1}`);
  };

  const handleNextPage = () => {
    router.push(`/notifications?pageNum=${page + 1}`);
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

        {loading ? (
          <div className="loading">Loading notifications...</div>
        ) : notifications.length === 0 ? (
          <p className="no-results">No notifications</p>
        ) : (
          <div className="notifications-list">
            {notifications.map((notification) => (
              <NotificationCard
                key={notification.id}
                notification={notification}
              />
            ))}
          </div>
        )}

        {/* Pagination */}
        {(showPrevious || showNext) && (
          <div className="pagination">
            {showPrevious && (
              <button onClick={handlePreviousPage} className="btn btn-secondary">
                Previous Page
              </button>
            )}
            {showNext && (
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

