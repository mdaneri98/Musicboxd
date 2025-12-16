import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import CommentCard from './CommentCard';
import { renderWithProviders } from '../../test-utils';
import { Comment } from '@/types';

// Mock delete action
jest.mock('@/store/slices', () => ({
    ...jest.requireActual('@/store/slices'),
    deleteCommentAsync: jest.fn().mockReturnValue({ type: 'comments/deleteComment/pending' }),
    selectCurrentUser: jest.fn(),
}));

// Mock repositories and utils
jest.mock('@/repositories', () => ({
    imageRepository: {
        getImageUrl: (id: string) => `http://mock-images/${id}`,
    },
}));
jest.mock('@/utils/timeUtils', () => ({
    formatTimeAgo: () => '2 hours ago',
}));

import { selectCurrentUser, deleteCommentAsync } from '@/store/slices';

describe('CommentCard', () => {
    const mockComment: Comment = {
        id: 1,
        content: 'Nice comment',
        user_id: 1,
        username: 'user1',
        created_at: '2023-01-01',
        user_image_id: 'img1',
        user_verified: true,
        user_moderator: false,
    };

    const mockUser = {
        id: 1,
        username: 'user1',
    };

    beforeEach(() => {
        (selectCurrentUser as unknown as jest.Mock).mockReturnValue(mockUser);
    });

    it('renders comment content', () => {
        renderWithProviders(<CommentCard comment={mockComment} />);

        expect(screen.getByText('Nice comment')).toBeInTheDocument();
        expect(screen.getByText('user1')).toBeInTheDocument();
        expect(screen.getByText('2 hours ago')).toBeInTheDocument();
        expect(screen.getByText('label.verified')).toBeInTheDocument();
    });

    it('shows delete/edit buttons for owner', () => {
        const onEdit = jest.fn();
        renderWithProviders(<CommentCard comment={mockComment} onEdit={onEdit} />);

        expect(screen.getByTitle('common.editComment')).toBeInTheDocument();
        expect(screen.getByTitle('common.deleteComment')).toBeInTheDocument();
    });

    it('does not show delete/edit buttons for non-owner', () => {
        (selectCurrentUser as unknown as jest.Mock).mockReturnValue({ id: 999 });

        renderWithProviders(<CommentCard comment={mockComment} />);

        expect(screen.queryByTitle('common.editComment')).not.toBeInTheDocument();
        expect(screen.queryByTitle('common.deleteComment')).not.toBeInTheDocument();
    });

    it('calls onEdit when edit button clicked', () => {
        const onEdit = jest.fn();
        renderWithProviders(<CommentCard comment={mockComment} onEdit={onEdit} />);

        fireEvent.click(screen.getByTitle('common.editComment'));
        expect(onEdit).toHaveBeenCalledWith(mockComment);
    });

    it('handles delete with confirmation', async () => {
        const windowConfirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
        const { store } = renderWithProviders(<CommentCard comment={mockComment} />);
        const dispatchSpy = jest.spyOn(store, 'dispatch');

        fireEvent.click(screen.getByTitle('common.deleteComment'));

        expect(windowConfirmSpy).toHaveBeenCalled();
        expect(deleteCommentAsync).toHaveBeenCalledWith(1);
        // Checking dispatch is tricky because the action is thunk/async.
        // simpler to check the action creator was called.
    });
});
