import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import NotificationCard from './NotificationCard';
import { renderWithProviders } from '../../test-utils';
import { Notification, NotificationTypeEnum } from '@/types';

// Mock specific logic
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock-images/${id}`,
    },
}));
jest.mock('@/utils/timeUtils', () => ({
    formatTimeAgo: () => '1 day ago',
}));

import { markAsReadAsync } from '@/store/slices';

// We need to mock the action creator to verify it's called
jest.mock('@/store/slices', () => ({
    ...jest.requireActual('@/store/slices'),
    markAsReadAsync: jest.fn().mockReturnValue({ type: 'notifications/markAsRead' }),
}));

describe('NotificationCard', () => {
    const mockNotification: Notification = {
        id: 1,
        type: NotificationTypeEnum.FOLLOW,
        is_read: false,
        created_at: '2023-01-01',
        trigger_user_id: 2,
        trigger_username: 'user2',
        trigger_user_image_id: 'img2',
        user_id: 1,
    };

    it('renders basic notification info', () => {
        renderWithProviders(<NotificationCard notification={mockNotification} />);

        // We expect translation key to be rendered or mocked value.
        // Our mock translations returns the key.
        // t('notifications.types.RELATIONSHIP', { ... })
        // The mock in setup returns `str`. But standard mock might not replace params?
        // Wait, our test-utils/setup mocks t function to return `str`.
        // It ignores params. So text will contain "notifications.types.RELATIONSHIP"

        expect(screen.getByText(/notifications.types.FOLLOW/)).toBeInTheDocument();
        expect(screen.getByText('1 day ago')).toBeInTheDocument();

        // Check styling for unread
        const container = screen.getByText('1 day ago').closest('.notification-item');
        expect(container).toHaveClass('unread');
    });

    it('renders read style', () => {
        const readNotif = { ...mockNotification, is_read: true };
        renderWithProviders(<NotificationCard notification={readNotif} />);

        const container = screen.getByText('1 day ago').closest('.notification-item');
        expect(container).toHaveClass('read');
    });

    it('dispatches markAsRead on click', () => {
        renderWithProviders(<NotificationCard notification={mockNotification} />);

        // Clicking the user link
        const link = screen.getByRole('link', { name: /user2/i });
        fireEvent.click(link);

        expect(markAsReadAsync).toHaveBeenCalledWith(1);
    });
});
