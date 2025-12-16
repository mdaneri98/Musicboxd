import React from 'react';
import { screen } from '@testing-library/react';
import FavoritesSection from './FavoritesSection';
import { renderWithProviders } from '../../test-utils';
import { Artist, Album, Song } from '@/types';

// Mock specific card components
jest.mock('@/components/cards', () => ({
    ArtistCard: ({ artist }: { artist: Artist }) => <div data-testid="artist-card">{artist.name}</div>,
    AlbumCard: ({ album }: { album: Album }) => <div data-testid="album-card">{album.title}</div>,
    SongCard: ({ song }: { song: Song }) => <div data-testid="song-card">{song.title}</div>,
}));

// Mock spinner
jest.mock('@/components/ui', () => ({
    LoadingSpinner: () => <div data-testid="loading-spinner">Loading...</div>,
}));

describe('FavoritesSection', () => {
    const mockArtist: Artist = { id: 1, name: 'Artist 1', bio: '', image_id: 'img1', rating_count: 0, avg_rating: 0 };
    const mockAlbum: Album = { id: 1, title: 'Album 1', artist_id: 1, genre: '', release_date: '', rating_count: 0, avg_rating: 0 };
    const mockSong: Song = { id: 1, title: 'Song 1', duration: 180, album_id: 1, avg_rating: 0, track_number: 1 };

    it('renders favorites', () => {
        renderWithProviders(
            <FavoritesSection
                favoriteArtists={[mockArtist]}
                favoriteAlbums={[mockAlbum]}
                favoriteSongs={[mockSong]}
                loading={false}
                isOwnProfile={false}
            />
        );

        expect(screen.getByText('profile.favoriteArtists')).toBeInTheDocument();
        expect(screen.getByTestId('artist-card')).toHaveTextContent('Artist 1');

        expect(screen.getByText('profile.favoriteAlbums')).toBeInTheDocument();
        expect(screen.getByTestId('album-card')).toHaveTextContent('Album 1');

        expect(screen.getByText('profile.favoriteSongs')).toBeInTheDocument();
        expect(screen.getByTestId('song-card')).toHaveTextContent('Song 1');
    });

    it('shows loading spinner', () => {
        renderWithProviders(
            <FavoritesSection
                favoriteArtists={[]}
                favoriteAlbums={[]}
                favoriteSongs={[]}
                loading={true}
                isOwnProfile={false}
            />
        );

        expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
    });

    it('shows empty state for visitor', () => {
        renderWithProviders(
            <FavoritesSection
                favoriteArtists={[]}
                favoriteAlbums={[]}
                favoriteSongs={[]}
                isOwnProfile={false}
            />
        );

        expect(screen.getByText('common.noFavoritesYet')).toBeInTheDocument();
    });

    it('shows add prompts for own profile', () => {
        renderWithProviders(
            <FavoritesSection
                favoriteArtists={[]}
                favoriteAlbums={[]}
                favoriteSongs={[]}
                isOwnProfile={true}
            />
        );

        expect(screen.getByText('profile.addFavoriteArtists')).toBeInTheDocument();
        expect(screen.getByText('profile.addFavoriteAlbums')).toBeInTheDocument();
        expect(screen.getByText('profile.addFavoriteSongs')).toBeInTheDocument();
    });
});
