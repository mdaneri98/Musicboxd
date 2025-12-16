import React from 'react';
import { screen } from '@testing-library/react';
import ArtistCard from './ArtistCard';
import { renderWithProviders } from '../../test-utils';
import { Artist } from '@/types';
import { ASSETS } from '@/utils';

// Mock the image repository
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock-api/images/${id}`,
    },
}));

describe('ArtistCard', () => {
    const mockArtist: Artist = {
        id: 1,
        name: 'Test Artist',
        bio: 'Bio',
        image_id: 'img1',
        avg_rating: 4.8,
    };

    it('renders artist information correctly', () => {
        renderWithProviders(<ArtistCard artist={mockArtist} />);

        expect(screen.getByText('Test Artist')).toBeInTheDocument();

        const img = screen.getByRole('img');
        expect(img).toHaveAttribute('src', 'http://mock-api/images/img1');
        expect(img).toHaveAttribute('alt', 'Test Artist');

        expect(screen.getByText('4.8')).toBeInTheDocument();
    });

    it('renders placeholder image when no image_id', () => {
        const artistNoImage = { ...mockArtist, image_id: undefined } as any;
        renderWithProviders(<ArtistCard artist={artistNoImage} />);

        const img = screen.getByRole('img');
        expect(img).toHaveAttribute('src', ASSETS.IMAGE_PLACEHOLDER);
    });

    it('does not render rating badge when rating is 0', () => {
        const artistNoRating = { ...mockArtist, avg_rating: 0 };
        renderWithProviders(<ArtistCard artist={artistNoRating} />);

        expect(screen.queryByText('0.0')).not.toBeInTheDocument();
    });

    it('links to the correct artist page', () => {
        renderWithProviders(<ArtistCard artist={mockArtist} />);

        const link = screen.getByRole('link');
        expect(link).toHaveAttribute('href', '/artists/1');
    });
});
