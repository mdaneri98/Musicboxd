import React from 'react';
import { screen } from '@testing-library/react';
import UserCard from './UserCard';
import { renderWithProviders } from '../../test-utils';
import { User } from '@/types';

// Mock repository
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock-images/${id}`,
    },
}));

describe('UserCard', () => {
    const mockUser: User = {
        id: 1,
        username: 'testuser',
        email: 'test@mail.com',
        name: 'Test Name',
        image_id: 'img1',
        enabled: true,
        locked: false,
        verified: true,
        moderator: true,
        review_amount: 5,
        followers_amount: 10,
        following_amount: 20,
        created_at: '',
    };

    it('renders user details and stats', () => {
        renderWithProviders(<UserCard user={mockUser} />);

        expect(screen.getByText('@testuser')).toBeInTheDocument();
        expect(screen.getByText('Test Name')).toBeInTheDocument();

        // Stats
        expect(screen.getByText('5')).toBeInTheDocument();
        expect(screen.getByText('10')).toBeInTheDocument();
        expect(screen.getByText('20')).toBeInTheDocument();

        // Badges
        expect(screen.getByText('label.verified')).toBeInTheDocument();
        expect(screen.getByText('label.moderator')).toBeInTheDocument();
    });

    it('links to user profile', () => {
        renderWithProviders(<UserCard user={mockUser} />);

        const link = screen.getByRole('link');
        expect(link).toHaveAttribute('href', '/users/1');
    });
});
