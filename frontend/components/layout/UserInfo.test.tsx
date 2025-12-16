import React from 'react';
import { screen } from '@testing-library/react';
import { UserInfo } from './UserInfo';
import { renderWithProviders } from '../../test-utils';
import { User } from '@/types';

// Mock repository
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock/${id}`,
    },
}));

describe('UserInfo', () => {
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
        review_amount: 10,
        followers_amount: 20,
        following_amount: 30,
        bio: 'Bio text',
        created_at: '',
    };

    it('renders user details', () => {
        renderWithProviders(
            <UserInfo
                user={mockUser}
                isOwnProfile={false}
                isAuthenticated={true}
                isFollowing={false}
                followLoading={false}
                onFollowToggle={jest.fn()}
            />
        );

        expect(screen.getByText('@testuser')).toBeInTheDocument();
        expect(screen.getByText('Test Name')).toBeInTheDocument();
        expect(screen.getByText('Bio text')).toBeInTheDocument();

        // Badges
        expect(screen.getByText('label.verified')).toBeInTheDocument();
        expect(screen.getByText('label.moderator')).toBeInTheDocument();

        // Stats
        expect(screen.getByText('10')).toBeInTheDocument();
        expect(screen.getByText('20')).toBeInTheDocument();
        expect(screen.getByText('30')).toBeInTheDocument();
    });

    it('shows edit button for own profile', () => {
        renderWithProviders(
            <UserInfo
                user={mockUser}
                isOwnProfile={true}
                isAuthenticated={true}
                isFollowing={false}
                followLoading={false}
                onFollowToggle={jest.fn()}
            />
        );

        expect(screen.getByText('label.editProfile')).toBeInTheDocument();
        expect(screen.queryByText('common.follow')).not.toBeInTheDocument();
    });

    it('shows follow button for others', () => {
        renderWithProviders(
            <UserInfo
                user={mockUser}
                isOwnProfile={false}
                isAuthenticated={true}
                isFollowing={false}
                followLoading={false}
                onFollowToggle={jest.fn()}
            />
        );

        expect(screen.getByText('common.follow')).toBeInTheDocument();
    });

    it('shows unfollow button when following', () => {
        renderWithProviders(
            <UserInfo
                user={mockUser}
                isOwnProfile={false}
                isAuthenticated={true}
                isFollowing={true}
                followLoading={false}
                onFollowToggle={jest.fn()}
            />
        );

        expect(screen.getByText('common.unfollow')).toBeInTheDocument();
    });

    it('shows login button when not authenticated', () => {
        renderWithProviders(
            <UserInfo
                user={mockUser}
                isOwnProfile={false}
                isAuthenticated={false}
                isFollowing={false}
                followLoading={false}
                onFollowToggle={jest.fn()}
            />
        );

        expect(screen.getByText('common.loginToFollow')).toBeInTheDocument();
    });
});
