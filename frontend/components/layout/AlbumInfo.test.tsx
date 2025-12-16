import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import { AlbumInfo } from './AlbumInfo';
import { renderWithProviders } from '../../test-utils';
import { Album, Artist, User } from '@/types';

// Mocks
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock/${id}`,
    },
}));

// Mock RatingCard as it is tested separately
jest.mock('@/components/ui', () => ({
    RatingCard: () => <div data-testid="rating-card">RatingCard</div>,
}));

describe('AlbumInfo', () => {
    const mockAlbum: Album = {
        id: 1,
        title: 'Test Album',
        artist_id: 1,
        genre: 'Rock',
        release_date: '2023-01-01',
        rating_count: 5,
        avg_rating: 4.5
    };

    const mockArtist: Artist = {
        id: 1,
        name: 'Test Artist',
        bio: 'Bio',
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

    it('renders album details', () => {
        renderWithProviders(
            <AlbumInfo
                album={mockAlbum}
                artist={mockArtist}
                currentUser={mockUser}
                isAuthenticated={true}
                isFavorite={false}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={jest.fn()}
            />
        );

        expect(screen.getByText('Test Album')).toBeInTheDocument();
        expect(screen.getByText('Test Artist')).toBeInTheDocument();
        expect(screen.getByText('Rock')).toBeInTheDocument();
        expect(screen.getByTestId('rating-card')).toBeInTheDocument();
    });

    it('shows moderator link when moderator', () => {
        const modUser = { ...mockUser, moderator: true };
        renderWithProviders(
            <AlbumInfo
                album={mockAlbum}
                artist={mockArtist}
                currentUser={modUser}
                isAuthenticated={true}
                isFavorite={false}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={jest.fn()}
            />
        );

        // Link with edit icon
        const link = screen.getByRole('link', { name: '' });
        // Icon might not have accessible name.
        // We can find by class or verify container.
        // The edit-link has href containing /moderator/music
        const links = screen.getAllByRole('link');
        const modLink = links.find(l => l.getAttribute('href')?.includes('/moderator/music'));
        expect(modLink).toBeInTheDocument();
    });

    it('handles favorite toggle', () => {
        const onToggle = jest.fn();
        renderWithProviders(
            <AlbumInfo
                album={mockAlbum}
                artist={mockArtist}
                currentUser={mockUser}
                isAuthenticated={true}
                isFavorite={false} // "Add to Favorites"
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={onToggle}
            />
        );

        fireEvent.click(screen.getByText('common.addToFavorites'));
        expect(onToggle).toHaveBeenCalled();
    });

    it('shows login link when not authenticated', () => {
        renderWithProviders(
            <AlbumInfo
                album={mockAlbum}
                artist={mockArtist}
                currentUser={null}
                isAuthenticated={false}
                isFavorite={false}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={jest.fn()}
            />
        );

        expect(screen.getByText('common.loginToAddFavorite')).toBeInTheDocument();
        expect(screen.queryByText('common.addToFavorites')).not.toBeInTheDocument();
    });
});
