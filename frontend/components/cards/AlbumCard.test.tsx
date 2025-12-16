import React from 'react';
import { screen } from '@testing-library/react';
import AlbumCard from './AlbumCard';
import { renderWithProviders } from '../../test-utils';
import { Album } from '@/types';
import { ASSETS } from '@/utils';

// Mock the image repository
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock-api/images/${id}`,
    },
}));

describe('AlbumCard', () => {
    const mockAlbum: Album = {
        id: 1,
        title: 'Test Album',
        description: 'Test Description',
        year: 2023,
        image_id: 'img1',
        avg_rating: 4.5,
        artist: { id: 1, name: 'Artist' } as any,
        genre: 'Rock',
    };

    it('renders album information correctly', () => {
        renderWithProviders(<AlbumCard album={mockAlbum} />);

        expect(screen.getByText('Test Album')).toBeInTheDocument();

        const img = screen.getByRole('img');
        expect(img).toHaveAttribute('src', 'http://mock-api/images/img1');
        expect(img).toHaveAttribute('alt', 'Test Album');

        expect(screen.getByText('4.5')).toBeInTheDocument();
    });

    it('renders placeholder image when no image_id', () => {
        const albumNoImage = { ...mockAlbum, image_id: undefined } as any; // Type hack if needed or strictly undefined
        renderWithProviders(<AlbumCard album={albumNoImage} />);

        const img = screen.getByRole('img');
        expect(img).toHaveAttribute('src', ASSETS.IMAGE_PLACEHOLDER);
    });

    it('does not render rating badge when rating is 0', () => {
        const albumNoRating = { ...mockAlbum, avg_rating: 0 };
        renderWithProviders(<AlbumCard album={albumNoRating} />);

        expect(screen.queryByText('0.0')).not.toBeInTheDocument();
    });

    it('links to the correct album page', () => {
        renderWithProviders(<AlbumCard album={mockAlbum} />);

        const link = screen.getByRole('link');
        expect(link).toHaveAttribute('href', '/albums/1');
    });
});
