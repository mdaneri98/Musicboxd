
/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import notificationReducer, {
    addNotification,
    removeNotification,
    clearAllNotifications,
    fetchNotificationsAsync,
    fetchMoreNotificationsAsync,
    fetchUnreadCountAsync,
    markAsReadAsync,
    markAllAsReadAsync,
    deleteNotificationAsync,
    NotificationState,
} from './notificationSlice';
import { Notification } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('notificationSlice', () => {
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

    const mockNotification: Notification = {
        id: 1,
        title: 'Test Notif',
        message: 'Hello',
        is_read: false,
        created_at: '2023-01-01',
        user_id: 1,
        // Add other required fields if any, based on types
        type: 'FOLLOW',
        resource_id: 123,
        resource_type: 'USER',
        action: 'follow',
    } as any; // Cast if type mismatch on minor optional fields

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(notificationReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle addNotification', () => {
            const actual = notificationReducer(initialState, addNotification(mockNotification));
            expect(actual.notifications[1]).toEqual(mockNotification);
            expect(actual.notificationIds).toContain(1);
            expect(actual.unreadCount).toBe(1);
        });

        it('should handle removeNotification', () => {
            const stateWithNotification: NotificationState = {
                ...initialState,
                notifications: { 1: mockNotification },
                notificationIds: [1],
                unreadCount: 1,
            };
            const actual = notificationReducer(stateWithNotification, removeNotification(1));
            expect(actual.notifications[1]).toBeUndefined();
            expect(actual.notificationIds).toHaveLength(0);
            expect(actual.unreadCount).toBe(0);
        });

        it('should handle clearAllNotifications', () => {
            const stateWithNotification: NotificationState = {
                ...initialState,
                notifications: { 1: mockNotification },
                notificationIds: [1],
                unreadCount: 1,
            };
            const actual = notificationReducer(stateWithNotification, clearAllNotifications());
            expect(actual.notifications).toEqual({});
            expect(actual.notificationIds).toHaveLength(0);
            expect(actual.unreadCount).toBe(0);
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ notifications: NotificationState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: {
                    notifications: notificationReducer,
                },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('fetchNotificationsAsync', () => {
            it('should fetch notifications successfully', async () => {
                const responseData = {
                    items: [{ data: mockNotification }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/notifications')
                    .query({ page: 1, size: 10 })
                    .reply(200, responseData);

                await store.dispatch(fetchNotificationsAsync({ page: 1, size: 10 }));

                const state = store.getState().notifications;
                expect(state.loading).toBe(false);
                expect(state.error).toBeNull();
                expect(state.notificationIds).toContain(1);
                expect(state.notifications[1]).toEqual(mockNotification);
            });

            it('should handle fetch failure', async () => {
                nock(API_BASE_URL)
                    .get('/notifications')
                    .query({ page: 1, size: 10 })
                    .reply(500, { message: 'Server Error' });

                await store.dispatch(fetchNotificationsAsync({ page: 1, size: 10 }));

                const state = store.getState().notifications;
                expect(state.loading).toBe(false);
                expect(state.error).toContain('Server Error');
            });
        });

        describe('fetchMoreNotificationsAsync', () => {
            it('should append notifications successfully', async () => {
                // Preload state
                store = configureStore({
                    reducer: { notifications: notificationReducer },
                    preloadedState: {
                        notifications: {
                            ...initialState,
                            notifications: { 1: mockNotification },
                            notificationIds: [1],
                            pagination: { ...initialState.pagination, page: 1 }
                        }
                    }
                });

                const newNotification = { ...mockNotification, id: 2 };
                const responseData = {
                    items: [{ data: newNotification }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/notifications')
                    .query({ page: 2, size: 10 })
                    .reply(200, responseData);

                await store.dispatch(fetchMoreNotificationsAsync({ page: 2, size: 10 }));

                const state = store.getState().notifications;
                expect(state.loadingMore).toBe(false);
                expect(state.notificationIds).toHaveLength(2);
                expect(state.notifications[2]).toBeDefined();
                expect(state.pagination.page).toBe(2);
            });

            it('should handle fetch more failure', async () => {
                nock(API_BASE_URL)
                    .get('/notifications')
                    .query({ page: 2, size: 10 })
                    .reply(500);

                await store.dispatch(fetchMoreNotificationsAsync({ page: 2, size: 10 }));

                const state = store.getState().notifications;
                expect(state.loadingMore).toBe(false);
                expect(state.error).toContain('Request failed with status code 500');
            });
        });

        describe('fetchUnreadCountAsync', () => {
            it('should fetch unread count successfully', async () => {
                // Mock getUnreadCount doing GET notifications?status=UNREAD&page=1&size=1
                // The repo implementation calls getNotifications(1, 1, UNREAD)
                const responseData = {
                    items: [],
                    currentPage: 1,
                    pageSize: 1,
                    totalCount: 5, // unread count from metadata
                };

                nock(API_BASE_URL)
                    .get('/notifications')
                    .query({ page: 1, size: 1, status: 'UNREAD' })
                    .reply(200, responseData);

                await store.dispatch(fetchUnreadCountAsync());

                const state = store.getState().notifications;
                expect(state.loadingCount).toBe(false);
                expect(state.unreadCount).toBe(5);
            });

            it('should handle unread count failure', async () => {
                nock(API_BASE_URL)
                    .get('/notifications')
                    .query(true) // match any query
                    .reply(500);

                await store.dispatch(fetchUnreadCountAsync());
                const state = store.getState().notifications;
                expect(state.error).toBeDefined();
            });
        });

        describe('markAsReadAsync', () => {
            it('should mark notification as read successfully', async () => {
                // Preload
                store = configureStore({
                    reducer: { notifications: notificationReducer },
                    preloadedState: {
                        notifications: {
                            ...initialState,
                            notifications: { 1: mockNotification }, // is_read: false
                            unreadCount: 1
                        }
                    }
                });

                nock(API_BASE_URL)
                    .patch('/notifications/1', { is_read: true })
                    .reply(200, {});

                await store.dispatch(markAsReadAsync(1));

                const state = store.getState().notifications;
                expect(state.notifications[1].is_read).toBe(true);
                expect(state.unreadCount).toBe(0);
            });

            it('should handle mark as read failure', async () => {
                nock(API_BASE_URL)
                    .patch('/notifications/1', { is_read: true })
                    .reply(500);

                await store.dispatch(markAsReadAsync(1));
                const state = store.getState().notifications;
                expect(state.error).toBeDefined();
            });
        });

        describe('markAllAsReadAsync', () => {
            it('should mark all as read successfully', async () => {
                store = configureStore({
                    reducer: { notifications: notificationReducer },
                    preloadedState: {
                        notifications: {
                            ...initialState,
                            notifications: {
                                1: { ...mockNotification, id: 1, is_read: false },
                                2: { ...mockNotification, id: 2, is_read: false }
                            },
                            unreadCount: 2
                        }
                    }
                });

                nock(API_BASE_URL)
                    .patch('/notifications', { is_read: true })
                    .reply(200, {});

                await store.dispatch(markAllAsReadAsync());

                const state = store.getState().notifications;
                expect(state.notifications[1].is_read).toBe(true);
                expect(state.notifications[2].is_read).toBe(true);
                expect(state.unreadCount).toBe(0);
            });

            it('should handle mark all read failure', async () => {
                nock(API_BASE_URL)
                    .patch('/notifications', { is_read: true })
                    .reply(500);

                await store.dispatch(markAllAsReadAsync());
                const state = store.getState().notifications;
                expect(state.error).toBeDefined();
            });
        });

        describe('deleteNotificationAsync', () => {
            it('should delete notification successfully', async () => {
                store = configureStore({
                    reducer: { notifications: notificationReducer },
                    preloadedState: {
                        notifications: {
                            ...initialState,
                            notifications: { 1: mockNotification },
                            notificationIds: [1],
                            unreadCount: 1,
                            pagination: { ...initialState.pagination, totalCount: 1 }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/notifications/1')
                    .reply(204);

                await store.dispatch(deleteNotificationAsync(1));

                const state = store.getState().notifications;
                expect(state.notifications[1]).toBeUndefined();
                expect(state.notificationIds).not.toContain(1);
                expect(state.unreadCount).toBe(0);
            });

            it('should handle delete failure', async () => {
                nock(API_BASE_URL)
                    .delete('/notifications/1')
                    .reply(500);

                await store.dispatch(deleteNotificationAsync(1));
                const state = store.getState().notifications;
                expect(state.error).toBeDefined();
            });
        });

    });
});
