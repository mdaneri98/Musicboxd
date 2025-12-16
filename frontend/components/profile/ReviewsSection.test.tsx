import React from 'react';
import { screen } from '@testing-library/react';
import ReviewsSection from './ReviewsSection';
import { renderWithProviders } from '../../test-utils';
import { Review } from '@/types';

// Mock child components
jest.mock('@/components/cards', () => ({
    ReviewCard: ({ review }: { review: Review }) => <div data-testid="review-card">{review.title}</div>,
}));

jest.mock('@/components/ui', () => ({
    LoadingSpinner: () => <div data-testid="loading-spinner">Loading...</div>,
}));

describe('ReviewsSection', () => {
    const mockReview: Review = {
        id: 1,
        title: 'Review 1',
        description: 'Desc',
        rating: 5,
        created_at: '',
        item_id: 1,
        item_type: 'ALBUM',
        item_name: 'Album',
        item_image_id: 'img',
        user_id: 1,
        user_username: 'user',
        user_image_id: 'img',
        likes: 0,
        liked_by_current_user: false
    };

    it('renders reviews', () => {
        renderWithProviders(
            <ReviewsSection
                reviews={[mockReview]}
                loading={false}
            />
        );

        expect(screen.getByTestId('review-card')).toHaveTextContent('Review 1');
    });

    it('shows empty state', () => {
        renderWithProviders(
            <ReviewsSection
                reviews={[]}
                loading={false}
            />
        );

        expect(screen.getByText('profile.noReviews')).toBeInTheDocument();
    });

    it('shows loading state', () => {
        renderWithProviders(
            <ReviewsSection
                reviews={[]}
                loading={true}
            />
        );

        // Should show spinner and NOT empty state
        expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
        expect(screen.queryByText('profile.noReviews')).not.toBeInTheDocument();
    });

    it('shows loading more', () => {
        renderWithProviders(
            <ReviewsSection
                reviews={[mockReview]}
                loading={false}
                loadingMore={true}
            />
        );

        expect(screen.getByTestId('review-card')).toBeInTheDocument();
        expect(screen.getByTestId('loading-spinner')).toBeInTheDocument(); // large or small mock doesn't distinguish but presence confirm
    });

    it('renders sentinel ref', () => {
        const ref = React.createRef<HTMLDivElement>();
        renderWithProviders(
            <ReviewsSection
                reviews={[mockReview]}
                sentinelRef={ref}
            />
        );

        // Sentinel is a div with class infinite-scroll-sentinel
        // We can't query ref directly in RTL easily, but class exists

        // Actually best way:
        const { container } = renderWithProviders(
            <ReviewsSection reviews={[mockReview]} sentinelRef={ref} />
        );
        // eslint-disable-next-line testing-library/no-container, testing-library/no-node-access
        expect(container.querySelector('.infinite-scroll-sentinel')).toBeInTheDocument();
    });
});
