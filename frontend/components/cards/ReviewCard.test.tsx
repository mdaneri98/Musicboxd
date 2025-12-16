import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import ReviewCard from './ReviewCard';
import { renderWithProviders } from '../../test-utils';
import { Review, ReviewItemTypeEnum } from '@/types';

// Mock deps
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock-images/${id}`,
    },
}));
jest.mock('@/utils/timeUtils', () => ({
    formatTimeAgo: () => 'yesterday',
}));

// Mock actions
import { likeReviewAsync, unlikeReviewAsync, blockReviewAsync } from '@/store/slices';
jest.mock('@/store/slices', () => ({
    ...jest.requireActual('@/store/slices'),
    likeReviewAsync: jest.fn().mockReturnValue({ type: 'like' }),
    unlikeReviewAsync: jest.fn().mockReturnValue({ type: 'unlike' }),
    blockReviewAsync: jest.fn().mockReturnValue({ type: 'block' }),
    selectIsModerator: jest.fn(),
    selectCurrentUser: jest.fn(),
}));
import { selectIsModerator, selectCurrentUser } from '@/store/slices';

describe('ReviewCard', () => {
    const mockReview: Review = {
        id: 1,
        title: 'Great Album',
        description: 'Really liked it',
        rating: 5,
        likes: 10,
        liked: false,
        username: 'reviewer',
        user_id: 100,
        created_at: '2023-01-01',
        item_id: 50,
        item_name: 'The Album',
        item_type: ReviewItemTypeEnum.ALBUM,
        item_image_id: 'img_a',
        is_blocked: false,
        comment_amount: 5,
        user_verified: false,
        user_moderator: false,
    };

    beforeEach(() => {
        (selectIsModerator as unknown as jest.Mock).mockReturnValue(false);
        (selectCurrentUser as unknown as jest.Mock).mockReturnValue({ id: 200 });
    });

    it('renders review details', () => {
        renderWithProviders(<ReviewCard review={mockReview} />);

        expect(screen.getByText('Great Album')).toBeInTheDocument();
        expect(screen.getByText('Really liked it')).toBeInTheDocument();
        expect(screen.getByText('The Album')).toBeInTheDocument();
        expect(screen.getByText('reviewer')).toBeInTheDocument();
        expect(screen.getByText('10')).toBeInTheDocument(); // likes
    });

    it('handles like action', () => {
        // Need to supply preloaded state or mocks? 
        // We rely on component dispatching the mocked action
        renderWithProviders(<ReviewCard review={mockReview} />);

        // Find like button - expecting not active
        const btn = screen.getAllByRole('button')[0]; // First button is usually like, but better check icon class
        // Or look for container logic
        // Structure: <button ...> <i class="fa... fa-heart"></i> </button>
        // We can just click the first button

        fireEvent.click(btn);
        expect(likeReviewAsync).toHaveBeenCalledWith(1);
    });

    it('handles unlike action', () => {
        renderWithProviders(<ReviewCard review={{ ...mockReview, liked: true }} />);

        const btn = screen.getAllByRole('button')[0];
        fireEvent.click(btn);
        expect(unlikeReviewAsync).toHaveBeenCalledWith(1);
    });

    it('shows blocked content for moderator', () => {
        (selectIsModerator as unknown as jest.Mock).mockReturnValue(true);
        const blockedReview = { ...mockReview, is_blocked: true };

        renderWithProviders(<ReviewCard review={blockedReview} />);

        expect(screen.getByText('review.blockedByModerator')).toBeInTheDocument();
        // Moderator sees the content
        expect(screen.getByText('Great Album')).toBeInTheDocument();
    });

    it('does not show blocked content for normal user', () => {
        (selectIsModerator as unknown as jest.Mock).mockReturnValue(false);
        const blockedReview = { ...mockReview, is_blocked: true };

        renderWithProviders(<ReviewCard review={blockedReview} />);

        expect(screen.getByText('review.blockedByModerator')).toBeInTheDocument();
        expect(screen.queryByText('Great Album')).not.toBeInTheDocument();
    });
});
