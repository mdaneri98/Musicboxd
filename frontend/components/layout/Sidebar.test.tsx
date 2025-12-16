import React from 'react';
import { screen, waitFor, fireEvent } from '@testing-library/react';
import Sidebar from './Sidebar';
import { renderWithProviders } from '../../test-utils';
import { useRouter } from 'next/router';

// Mock router
jest.mock('next/router', () => ({
    useRouter: jest.fn(),
}));

// Mock repositories if needed (imageRepository used)
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock/${id}`,
    },
}));

describe('Sidebar', () => {
    const mockPush = jest.fn();

    beforeEach(() => {
        (useRouter as jest.Mock).mockReturnValue({
            push: mockPush,
        });
    });

    it('renders public links', () => {
        renderWithProviders(<Sidebar />);

        expect(screen.getByTitle('navbar.home')).toBeInTheDocument();
        expect(screen.getByTitle('navbar.discovery')).toBeInTheDocument();
        expect(screen.getByTitle('navbar.search')).toBeInTheDocument();
    });

    it('shows login link when unauthenticated', () => {
        renderWithProviders(<Sidebar />, {
            preloadedState: {
                auth: { token: null, isAuthenticated: false, isInitializing: false, error: null },
            }
        });

        expect(screen.getByTitle('navbar.login')).toBeInTheDocument();
        expect(screen.queryByTitle('navbar.logout')).not.toBeInTheDocument();
    });

    it('shows authenticated links and notifications', () => {
        renderWithProviders(<Sidebar />, {
            preloadedState: {
                auth: {
                    currentUser: { id: 1, username: 'me', image_id: 'img1' } as any,
                    jwt: { accessToken: 'token', refreshToken: 'ref' },
                    isAuthenticated: true,
                    initializing: false,
                    loading: false,
                    error: null,
                    isModerator: false
                },
                notifications: { unreadCount: 5, notifications: [], loading: false, error: null, hasMore: false, page: 0 },
            }
        });

        expect(screen.getByTitle('navbar.notifications')).toBeInTheDocument();
        expect(screen.getByText('5')).toBeInTheDocument(); // Badge
        expect(screen.getByTitle('navbar.profile')).toBeInTheDocument();
        expect(screen.getByTitle('navbar.settings')).toBeInTheDocument();
        expect(screen.getByTitle('navbar.logout')).toBeInTheDocument();
    });

    it('handles logout', async () => {
        const { user: userEvent } = renderWithProviders(<Sidebar />, {
            preloadedState: {
                auth: {
                    currentUser: { id: 1, username: 'me' } as any,
                    jwt: { accessToken: 'token', refreshToken: 'ref' },
                    isAuthenticated: true,
                    initializing: false,
                    loading: false,
                    error: null,
                    isModerator: false
                },
            }
        });

        await userEvent.click(screen.getByTitle('navbar.logout'));

        await waitFor(() => {
            // It dispatches logoutAsync and pushes '/'
            expect(mockPush).toHaveBeenCalledWith('/');
        });
    });
});
