/**
 * Notification Repository
 * Handles notification-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { buildUrl } from '@/utils/halHelpers';
import {
  Notification,
  Collection,
  HALResource,
  PaginationParams,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const NOTIFICATION_ENDPOINTS = {
  NOTIFICATIONS: '/notifications',
  NOTIFICATION_BY_ID: (id: number) => `/notifications/${id}`,
  MARK_AS_READ: (id: number) => `/notifications/${id}/read`,
  MARK_ALL_AS_READ: '/notifications/read-all',
  UNREAD_COUNT: '/notifications/unread-count',
};

// ============================================================================
// Notification Repository Class
// ============================================================================

class NotificationRepository {
  /**
   * Get paginated list of notifications
   * @param page Page number (0-indexed)
   * @param size Page size
   * @returns Collection of notifications with pagination metadata
   */
  async getNotifications(
    page: number = 1,
    size: number = 20
  ): Promise<Collection<HALResource<Notification>>> {
    try {
      const params: PaginationParams = { page, size };
      const url = buildUrl(NOTIFICATION_ENDPOINTS.NOTIFICATIONS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Notification>> = await apiClient.getCollection<Notification>(url);

        if (!response) {
        throw new Error('Invalid notifications response: missing data');
      }

      return response as Collection<HALResource<Notification>>;
    } catch (error) {
      console.error('Get notifications error:', error);
      throw error;
    }
  }

  /**
   * Get notification by ID
   * @param id Notification ID
   * @returns Notification data
   */
  async getNotificationById(id: number): Promise<HALResource<Notification>> {
    try {
      const response: HALResource<Notification> = await apiClient.getResource<Notification>(
        NOTIFICATION_ENDPOINTS.NOTIFICATION_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid notification response: missing data');
      }

      return response as HALResource<Notification>;
    } catch (error) {
      console.error(`Get notification ${id} error:`, error);
      throw error;
    }
  }

  // /**
  //  * Create a new notification
  //  * @param notificationData Notification data
  //  * @returns Created notification
  //  */
  // async createNotification(notificationData: Partial<Notification>): Promise<HALResource<Notification>> {
  //   try {
  //     const response: HALResource<Notification> = await apiClient.postResource<Notification>(
  //       NOTIFICATION_ENDPOINTS.NOTIFICATIONS,
  //       notificationData
  //     );

  //     if (!response) {
  //       throw new Error('Invalid create notification response: missing data');
  //     }

  //     return response as HALResource<Notification>;
  //   } catch (error) {
  //     console.error('Create notification error:', error);
  //     throw error;
  //   }
  // }

  // /**
  //  * Update notification
  //  * @param id Notification ID
  //  * @param notificationData Updated notification data
  //  * @returns Updated notification
  //  */
  // async updateNotification(id: number, notificationData: Partial<Notification>): Promise<HALResource<Notification>> {
  //   try {
  //     const response: HALResource<Notification> = await apiClient.putResource<Notification>(
  //       NOTIFICATION_ENDPOINTS.NOTIFICATION_BY_ID(id),
  //       notificationData
  //     );

  //     if (!response) {
  //       throw new Error('Invalid update notification response: missing data');
  //     }

  //     return response as HALResource<Notification>;
  //   } catch (error) {
  //     console.error(`Update notification ${id} error:`, error);
  //     throw error;
  //   }
  // }

  /**
   * Delete notification
   * @param id Notification ID
   */
  async deleteNotification(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Notification>(NOTIFICATION_ENDPOINTS.NOTIFICATION_BY_ID(id));
    } catch (error) {
      console.error(`Delete notification ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Mark notification as read
   * @param id Notification ID
   */
  async markAsRead(id: number): Promise<void> {
    try {
      await apiClient.postResource<Notification>(NOTIFICATION_ENDPOINTS.MARK_AS_READ(id));
    } catch (error) {
      console.error(`Mark notification ${id} as read error:`, error);
      throw error;
    }
  }

  /**
   * Mark all notifications as read
   */
  async markAllAsRead(): Promise<void> {
    try {
      await apiClient.patch<Notification>(NOTIFICATION_ENDPOINTS.NOTIFICATIONS, {markAllAsRead: true});
    } catch (error) {
      console.error('Mark all notifications as read error:', error);
      throw error;
    }
  }

  /**
   * Get count of unread notifications
   * @returns Unread count
   */
  // async getUnreadCount(): Promise<number> {
  //   try {
  //     const response: { unread_count: number } = await apiClient.get<{ unread_count: number }>(
  //       NOTIFICATION_ENDPOINTS.UNREAD_COUNT
  //     );

  //     if (!response) {
  //       throw new Error('Invalid unread count response: missing data');
  //     }

  //     return response.unread_count ?? 0;
  //   } catch (error) {
  //     console.error('Get unread count error:', error);
  //     throw error;
  //   }
  // }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const notificationRepository = new NotificationRepository();

