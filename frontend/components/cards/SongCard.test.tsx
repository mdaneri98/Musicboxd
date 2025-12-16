import React from 'react';
import { screen } from '@testing-library/react';
import SongCard from './SongCard';
import { renderWithProviders } from '../../test-utils';
import { Song } from '@/types';

describe('SongCard', () => {
    const mockSong: Song = {
        id: 1,
        title: 'Test Song',
        duration: 180,
        album_id: 1,
        avg_rating: 4.2,
        track_number: 1,
    };

    it('renders song info correctly', () => {
        renderWithProviders(<SongCard song={mockSong} index={0} />);

        expect(screen.getByText('1')).toBeInTheDocument(); // index + 1
        expect(screen.getByText('Test Song')).toBeInTheDocument();
        expect(screen.getByText('4.2')).toBeInTheDocument();
    });

    it('links to song details', () => {
        renderWithProviders(<SongCard song={mockSong} index={2} />);

        expect(screen.getByText('3')).toBeInTheDocument(); // 2 + 1
        const link = screen.getByRole('link');
        expect(link).toHaveAttribute('href', '/songs/1');
    });

    it('does not render rating when 0', () => {
        const noRatingSong = { ...mockSong, avg_rating: 0 };
        renderWithProviders(<SongCard song={noRatingSong} index={0} />);

        expect(screen.queryByText('0.0')).not.toBeInTheDocument();
    });
});
