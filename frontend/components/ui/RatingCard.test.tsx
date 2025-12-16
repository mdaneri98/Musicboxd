import React from 'react';
import { screen } from '@testing-library/react';
import RatingCard from './RatingCard';
import { renderWithProviders } from '../../test-utils';

describe('RatingCard', () => {
    const defaultProps = {
        totalRatings: 10,
        averageRating: 4.5,
        userRating: 0,
        reviewed: false,
        entityType: 'albums' as const,
        entityId: 123,
        entityLabel: 'Album',
    };

    test('renders stats correctly', () => {
        renderWithProviders(<RatingCard {...defaultProps} />);

        expect(screen.getByText('10')).toBeInTheDocument();
        expect(screen.getByText('4.50')).toBeInTheDocument();
        expect(screen.getByText('common.totalRatings')).toBeInTheDocument();
    });

    test('shows login button when not authenticated', () => {
        renderWithProviders(<RatingCard {...defaultProps} />, {
            preloadedState: {
                auth: { isAuthenticated: false, user: null, initializing: false, error: null }
            }
        });

        expect(screen.getByText('common.loginToReview')).toBeInTheDocument();
        expect(screen.getByRole('link', { name: /loginToReview/i })).toHaveAttribute('href', '/login');
    });

    test('shows rate button when authenticated and not reviewed', () => {
        renderWithProviders(<RatingCard {...defaultProps} />, {
            preloadedState: {
                auth: { isAuthenticated: true, user: { id: 1, username: 'test', email: 't@t.com' } as any, initializing: false, error: null }
            }
        });

        expect(screen.getByText('common.rateThis.Album')).toBeInTheDocument();
        expect(screen.getByRole('link')).toHaveAttribute('href', '/albums/123/reviews');
    });

    test('shows edit button when authenticated and reviewed', () => {
        renderWithProviders(<RatingCard {...defaultProps} reviewed={true} userRating={5} />, {
            preloadedState: {
                auth: { isAuthenticated: true, user: { id: 1 } as any, initializing: false, error: null }
            }
        });

        expect(screen.getByText('common.editYourReview')).toBeInTheDocument();
        expect(screen.getByRole('link')).toHaveAttribute('href', '/albums/123/reviews');

        // Find the "Your Rating" label and check closely for the value 5
        // The value 5 is in a sibling div to the label
        const label = screen.getByText('common.yourRating');
        const parent = label.parentElement;
        expect(parent).toHaveTextContent('5');
    });
});
