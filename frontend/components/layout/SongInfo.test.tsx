import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import { SongInfo } from './SongInfo';
import { renderWithProviders } from '../../test-utils';
import { Song, Album, Artist, User } from '@/types';

// Mocks
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock/${id}`,
    },
}));

jest.mock('@/components/ui', () => ({
    RatingCard: () => <div data-testid="rating-card">RatingCard</div>,
}));

describe('SongInfo', () => {
    const mockSong: Song = {
        id: 1,
        title: 'Test Song',
        duration: 180,
        album_id: 1,
        artist_id: 1,
        rating_count: 5,
        avg_rating: 4.5
    };

    const mockAlbum: Album = {
        id: 1,
        title: 'Test Album',
        artist_id: 1,
        genre: 'Rock',
        release_date: '2023-01-01'
    };

    const mockArtist: Artist = {
        id: 1,
        name: 'Test Artist',
        bio: 'Bio',
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

    it('renders song details', () => {
        renderWithProviders(
            <SongInfo
                song={mockSong}
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

        expect(screen.getByText('Test Song')).toBeInTheDocument();
        expect(screen.getByText('Test Album')).toBeInTheDocument();
        expect(screen.getByText('Test Artist')).toBeInTheDocument();
        expect(screen.getByTestId('rating-card')).toBeInTheDocument();
    });

    it('shows moderator link when moderator', () => {
        const modUser = { ...mockUser, moderator: true };
        renderWithProviders(
            <SongInfo
                song={mockSong}
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

        const links = screen.getAllByRole('link');
        const modLink = links.find(l => l.getAttribute('href')?.includes('/moderator/music'));
        expect(modLink).toBeInTheDocument();
    });

    it('handles favorite toggle', () => {
        const onToggle = jest.fn();
        renderWithProviders(
            <SongInfo
                song={mockSong}
                album={mockAlbum}
                artist={mockArtist}
                currentUser={mockUser}
                isAuthenticated={true}
                isFavorite={false}
                favoriteLoading={false}
                isReviewed={false}
                onFavoriteToggle={onToggle}
            />
        );

        fireEvent.click(screen.getByText('common.addToFavorites'));
        expect(onToggle).toHaveBeenCalled();
    });
});
