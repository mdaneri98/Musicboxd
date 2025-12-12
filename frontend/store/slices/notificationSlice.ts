/**
 * Notification Slice
 * Redux slice for notification state management
 */

import { createSlice, createAsyncThunk, PayloadAction, createSelector } from '@reduxjs/toolkit';
import { notificationRepository } from '@/repositories';
import { Notification, Collection, HALResource } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface NotificationState {
  // Notifications by ID (normalized state)
  notifications: Record<number, Notification>;
  // List of notification IDs for ordered display
  notificationIds: number[];
  // Unread notification count
  unreadCount: number;
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
    hasMore: boolean;
  };
  // Loading states
  loading: boolean;
  loadingMore: boolean;
  loadingCount: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: NotificationState = {
  notifications: {},
  notificationIds: [],
  unreadCount: 0,
  pagination: {
    page: 1,
    size: 10,
    totalCount: 0,
    hasMore: true,
  },
  loading: false,
  loadingMore: false,
  loadingCount: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated notifications list
 */
export const fetchNotificationsAsync = createAsyncThunk<
  Collection<HALResource<Notification>>,
  { page?: number; size?: number },
  { rejectValue: string }
>('notifications/fetchNotificationsAsync', async ({ page = 1, size = 10 }, { rejectWithValue }) => {
  try {
    const response = await notificationRepository.getNotifications(page, size);
    return response as Collection<HALResource<Notification>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch notifications');
  }
});

/**
 * Fetch more notifications (for infinite scroll)
 */
export const fetchMoreNotificationsAsync = createAsyncThunk<
  Collection<HALResource<Notification>>,
  { page: number; size?: number },
  { rejectValue: string }
>('notifications/fetchMoreNotificationsAsync', async ({ page, size = 10 }, { rejectWithValue }) => {
  try {
    const response = await notificationRepository.getNotifications(page, size);
    return response as Collection<HALResource<Notification>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more notifications');
  }
});

/**
 * Fetch unread notification count
 */
export const fetchUnreadCountAsync = createAsyncThunk<
  number,
  void,
  { rejectValue: string }
>('notifications/fetchUnreadCountAsync', async (_, { rejectWithValue }) => {
  try {
    const response = await notificationRepository.getUnreadCount();
    return response as number;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch unread count');
  }
});

/**
 * Mark notification as read
 */
export const markAsReadAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('notifications/markAsRead', async (notificationId, { rejectWithValue }) => {
  try {
    await notificationRepository.markAsRead(notificationId);
    return notificationId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to mark notification as read');
  }
});

/**
 * Mark all notifications as read
 */
export const markAllAsReadAsync = createAsyncThunk<
  void,
  void,
  { rejectValue: string }
>('notifications/markAllAsRead', async (_, { rejectWithValue }) => {
  try {
    await notificationRepository.markAllAsRead();
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to mark all notifications as read');
  }
});

/**
 * Delete notification
 */
export const deleteNotificationAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('notifications/deleteNotification', async (notificationId, { rejectWithValue }) => {
  try {
    await notificationRepository.deleteNotification(notificationId);
    return notificationId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete notification');
  }
});

// ============================================================================
// Slice
// ============================================================================

const notificationSlice = createSlice({
  name: 'notifications',
  initialState,
  reducers: {
    /**
     * Clear error state
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Add notification (e.g., from WebSocket/SSE)
     */
    addNotification: (state, action: PayloadAction<Notification>) => {
      const notification = action.payload;
      state.notifications[notification.id] = notification;
      // Add to beginning of the list
      state.notificationIds = [notification.id, ...state.notificationIds];
      // Update unread count if notification is unread
      if (!notification.is_read) {
        state.unreadCount += 1;
      }
      state.pagination.totalCount += 1;
    },

    /**
     * Remove notification
     */
    removeNotification: (state, action: PayloadAction<number>) => {
      const notificationId = action.payload;
      const notification = state.notifications[notificationId];
      
      // Update unread count if notification was unread
      if (notification && !notification.is_read) {
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
      
      delete state.notifications[notificationId];
      state.notificationIds = state.notificationIds.filter((id) => id !== notificationId);
      state.pagination.totalCount = Math.max(0, state.pagination.totalCount - 1);
    },

    /**
     * Clear all notifications
     */
    clearAllNotifications: (state) => {
      state.notifications = {};
      state.notificationIds = [];
      state.unreadCount = 0;
      state.pagination = {
        page: 1,
        size: 10,
        totalCount: 0,
        hasMore: true,
      };
    },
  },
  extraReducers: (builder) => {
    // Fetch Notifications (initial load - clears existing data)
    builder
      .addCase(fetchNotificationsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchNotificationsAsync.fulfilled, (state, action) => {
        state.loading = false;
        // Clear existing data for initial load
        state.notifications = {};
        state.notificationIds = [];
        
        action.payload.items.forEach((notification) => {
          state.notifications[notification.data.id] = notification.data as Notification;
          state.notificationIds.push(notification.data.id);
        });
        
        // Calculate hasMore: check if there are more items to load
        const loadedCount = action.payload.currentPage * action.payload.pageSize;
        const hasMore = loadedCount < action.payload.totalCount && action.payload.items.length === action.payload.pageSize;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchNotificationsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch notifications';
      });

    // Fetch More Notifications (infinite scroll - appends data)
    builder
      .addCase(fetchMoreNotificationsAsync.pending, (state) => {
        state.loadingMore = true;
        state.error = null;
      })
      .addCase(fetchMoreNotificationsAsync.fulfilled, (state, action) => {
        state.loadingMore = false;
        
        action.payload.items.forEach((notification) => {
          if (!state.notifications[notification.data.id]) {
            state.notifications[notification.data.id] = notification.data as Notification;
            state.notificationIds.push(notification.data.id);
          }
        });
        
        // Calculate hasMore: check if there are more items to load
        const loadedCount = action.payload.currentPage * action.payload.pageSize;
        const hasMore = loadedCount < action.payload.totalCount && action.payload.items.length === action.payload.pageSize;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreNotificationsAsync.rejected, (state, action) => {
        state.loadingMore = false;
        state.error = action.payload || 'Failed to fetch more notifications';
        // Stop infinite scroll on error to prevent retry loops
        state.pagination.hasMore = false;
      });

    // Fetch Unread Count
    builder
      .addCase(fetchUnreadCountAsync.pending, (state) => {
        state.loadingCount = true;
        state.error = null;
      })
      .addCase(fetchUnreadCountAsync.fulfilled, (state, action) => {
        state.loadingCount = false;
        state.unreadCount = action.payload;
      })
      .addCase(fetchUnreadCountAsync.rejected, (state, action) => {
        state.loadingCount = false;
        state.error = action.payload || 'Failed to fetch unread count';
      });

    // Mark As Read
    builder
      .addCase(markAsReadAsync.fulfilled, (state, action) => {
        const notificationId = action.payload;
        const notification = state.notifications[notificationId];
        
        if (notification && !notification.is_read) {
          notification.is_read = true;
          state.unreadCount = Math.max(0, state.unreadCount - 1);
        }
      })
      .addCase(markAsReadAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to mark notification as read';
      });

    // Mark All As Read
    builder
      .addCase(markAllAsReadAsync.fulfilled, (state) => {
        // Mark all notifications as read
        Object.values(state.notifications).forEach((notification) => {
          notification.is_read = true;
        });
        state.unreadCount = 0;
      })
      .addCase(markAllAsReadAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to mark all notifications as read';
      });

    // Delete Notification
    builder
      .addCase(deleteNotificationAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteNotificationAsync.fulfilled, (state, action) => {
        state.loading = false;
        const notificationId = action.payload;
        const notification = state.notifications[notificationId];
        
        // Update unread count if notification was unread
        if (notification && !notification.is_read) {
          state.unreadCount = Math.max(0, state.unreadCount - 1);
        }
        
        delete state.notifications[notificationId];
        state.notificationIds = state.notificationIds.filter((id) => id !== notificationId);
        state.pagination.totalCount = Math.max(0, state.pagination.totalCount - 1);
      })
      .addCase(deleteNotificationAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete notification';
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, addNotification, removeNotification, clearAllNotifications } =
  notificationSlice.actions;

// Selectors
export const selectNotifications = (state: RootState) => state.notifications.notifications;
export const selectNotificationById = (notificationId: number) => (state: RootState) =>
  state.notifications.notifications[notificationId] || null;
export const selectNotificationIds = (state: RootState) => state.notifications.notificationIds;
export const selectOrderedNotifications = createSelector(
  [selectNotifications, selectNotificationIds],
  (notifications, ids) => ids.map((id) => notifications[id]).filter(Boolean)
);
export const selectUnreadNotifications = (state: RootState) =>
  state.notifications.notificationIds
    .map((id) => state.notifications.notifications[id])
    .filter((notification) => !notification.is_read);
export const selectUnreadCount = (state: RootState) => state.notifications.unreadCount;
export const selectNotificationPagination = (state: RootState) => state.notifications.pagination;
export const selectNotificationLoading = (state: RootState) => state.notifications.loading;
export const selectNotificationLoadingMore = (state: RootState) => state.notifications.loadingMore;
export const selectNotificationError = (state: RootState) => state.notifications.error;
export const selectLoadingCount = (state: RootState) => state.notifications.loadingCount;
export const selectNotificationsHasMore = (state: RootState) => state.notifications.pagination.hasMore;

export default notificationSlice.reducer;

