import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import AuthProvider from './AuthProvider';
import { renderWithProviders } from '../test-utils';
import { authRepository, notificationRepository } from '@/repositories';

// Mock repositories
jest.mock('@/repositories', () => ({
    authRepository: {
        getAccessToken: jest.fn(),
        getRefreshToken: jest.fn(),
        getCurrentUser: jest.fn(),
        clearAuth: jest.fn(),
    },
    notificationRepository: {
        getUnreadCount: jest.fn(),
    }
}));

// Mock LoadingSpinner
jest.mock('@/components/ui', () => ({
    LoadingSpinner: () => <div data-testid="loading-spinner">Loading...</div>,
}));

describe('AuthProvider', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('initializes auth on mount', async () => {
        // Mock auth check to hang or return something
        (authRepository.getAccessToken as jest.Mock).mockReturnValue('token');
        (authRepository.getRefreshToken as jest.Mock).mockReturnValue('refresh');
        (authRepository.getCurrentUser as jest.Mock).mockResolvedValue({
            data: { id: 1, username: 'user', moderator: false }
        });
        (notificationRepository.getUnreadCount as jest.Mock).mockResolvedValue({ count: 5 });

        renderWithProviders(
            <AuthProvider>
                <div>Child Content</div>
            </AuthProvider>
            // No preloadedState, let it run initial check
        );

        // Initially initializing is true in state
        // We expect loading spinner
        expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();

        // Wait for it to resolve
        await waitFor(() => {
            expect(screen.getByText('Child Content')).toBeInTheDocument();
        });
    });

    it('renders children after initialization', async () => {
        renderWithProviders(
            <AuthProvider>
                <div>Child Content</div>
            </AuthProvider>,
            {
                preloadedState: {
                    auth: {
                        initializing: false,
                        isAuthenticated: false,
                        currentUser: null,
                        jwt: { accessToken: null, refreshToken: null },
                        loading: false,
                        error: null,
                        isModerator: false
                    }
                }
            }
        );

        await waitFor(() => {
            expect(screen.getByText('Child Content')).toBeInTheDocument();
        });
    });
});
