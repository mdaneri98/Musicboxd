import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import { ArtistInfo } from './ArtistInfo';
import { renderWithProviders } from '../../test-utils';
import { Artist, User } from '@/types';

// Mocks
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock/${id}`,
    },
}));

jest.mock('@/components/ui', () => ({
    RatingCard: () => <div data-testid="rating-card">RatingCard</div>,
}));

describe('ArtistInfo', () => {
    const mockArtist: Artist = {
        id: 1,
        name: 'Test Artist',
        bio: 'Artist Bio',
        image_id: 'img1',
        rating_count: 10,
        avg_rating: 4.0
    };

    const mockUser: User = {
        id: 1,
        username: 'user',
        email: 'u@mail.com',
        moderator: false,
        verified: false,
        enabled: true,
        locked: false,
        image_id: null,
        created_at: '',
        review_amount: 0,
        followers_amount: 0,
        following_amount: 0
    };

    it('renders artist details', () => {
        renderWithProviders(
            <ArtistInfo
                artist={mockArtist}
                currentUser={mockUser}
                isAuthenticated={true}
                isFavorite={false}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={jest.fn()}
            />
        );

        expect(screen.getByText('Test Artist')).toBeInTheDocument();
        expect(screen.getByText('Artist Bio')).toBeInTheDocument();
        expect(screen.getByTestId('rating-card')).toBeInTheDocument();
    });

    it('shows moderator link when moderator', () => {
        const modUser = { ...mockUser, moderator: true };
        renderWithProviders(
            <ArtistInfo
                artist={mockArtist}
                currentUser={modUser}
                isAuthenticated={true}
                isFavorite={false}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={jest.fn()}
            />
        );

        const links = screen.getAllByRole('link');
        const modLink = links.find(l => l.getAttribute('href')?.includes('/moderator/music'));
        expect(modLink).toBeInTheDocument();
    });

    it('handles favorite toggle', () => {
        const onToggle = jest.fn();
        renderWithProviders(
            <ArtistInfo
                artist={mockArtist}
                currentUser={mockUser}
                isAuthenticated={true}
                isFavorite={true}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={onToggle}
            />
        );

        fireEvent.click(screen.getByText('common.removeFromFavorites'));
        expect(onToggle).toHaveBeenCalled();
    });
});
