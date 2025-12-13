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
  NotificationStatusEnum,
} from '@/types';

// ============================================================================
// Type Definitions
// ============================================================================

interface NotificationParams extends PaginationParams {
  status?: NotificationStatusEnum;
}

// ============================================================================
// API Endpoints
// ============================================================================

const NOTIFICATION_ENDPOINTS = {
  NOTIFICATIONS: '/notifications',
  NOTIFICATION_BY_ID: (id: number) => `/notifications/${id}`,
};

// ============================================================================
// Notification Repository Class
// ============================================================================

class NotificationRepository {
  /**
   * Get paginated list of notifications
   * @param page Page number (1-indexed)
   * @param size Page size
   * @param status Optional status filter (READ, UNREAD, ALL)
   * @returns Collection of notifications with pagination metadata
   */
  async getNotifications(
    page: number = 1,
    size: number = 10,
    status?: NotificationStatusEnum
  ): Promise<Collection<HALResource<Notification>>> {
    try {
      const params: NotificationParams = { page, size };
      if (status) {
        params.status = status;
      }
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
      await apiClient.patchResource<Notification>(NOTIFICATION_ENDPOINTS.NOTIFICATION_BY_ID(id), {is_read: true});
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
      await apiClient.patchResource<Notification>(NOTIFICATION_ENDPOINTS.NOTIFICATIONS, {is_read: true});
    } catch (error) {
      console.error('Mark all notifications as read error:', error);
      throw error;
    }
  }

  /**
   * Get count of unread notifications
   * Uses the notifications endpoint with status=UNREAD and extracts totalCount
   * @returns Unread count
   */
  async getUnreadCount(): Promise<number> {
    try {
      // Fetch notifications with status=UNREAD, only need 1 item to get totalCount
      const response = await this.getNotifications(1, 1, NotificationStatusEnum.UNREAD);
      return response.totalCount ?? 0;
    } catch (error) {
      console.error('Get unread count error:', error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const notificationRepository = new NotificationRepository();

