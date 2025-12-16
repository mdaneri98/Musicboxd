import React from 'react';
import { screen, waitFor, fireEvent } from '@testing-library/react';
import ReviewForm from './ReviewForm';
import { renderWithProviders } from '../../test-utils';

describe('ReviewForm', () => {
    it('renders inputs', () => {
        renderWithProviders(<ReviewForm onSubmit={jest.fn()} onCancel={jest.fn()} />);

        expect(screen.getByLabelText('review.title')).toBeInTheDocument();
        expect(screen.getByLabelText('review.description')).toBeInTheDocument();
        expect(screen.getByText('review.rating')).toBeInTheDocument();
        // Star inputs have labels star5, star4...
        // But they use htmlFor. Labels contain unicode star.
        // "star5" might be hidden or styled? 
        // They are radio buttons.
        expect(screen.getAllByRole('radio')).toHaveLength(5);
    });

    it('validates required fields', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<ReviewForm onSubmit={onSubmit} onCancel={jest.fn()} />);

        await user.click(screen.getByRole('button', { name: 'review.submit' }));

        await waitFor(() => {
            expect(onSubmit).not.toHaveBeenCalled();
        });
    });

    it('submits valid data', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<ReviewForm onSubmit={onSubmit} onCancel={jest.fn()} />);

        await user.type(screen.getByLabelText('review.title'), 'My Review Title');
        await user.type(screen.getByLabelText('review.description'), 'This is a detailed description of the review.');

        // Click star 5.
        // Label htmlFor="star5"
        // We can click the label or the radio if visible.
        // Usually stars are styled labels hiding radios.
        // FireEvent click on label?
        const starLabel = screen.getAllByText('★')[0]; // First star symbol?
        // The code uses unicode &#9733; which is ★
        // But there are 5 of them.
        // Let's assume clicking the radio directly is easiest if UserEvent supports it.
        // Or simpler: click the radio input if we can find it by ID.
        // `star5` ID.

        // We can access input by label text if it was unique, but they are all same char.
        // Use container query or ID.
        // Let's use `container.querySelector('#star5')`?
        // Or just fireEvent click on the label associated with star 5.

        // Let's try finding by ID using document query since global is fine in jsdom
        const star5 = document.getElementById('star5');
        if (star5) fireEvent.click(star5);

        await user.click(screen.getByRole('button', { name: 'review.submit' }));

        await waitFor(() => {
            expect(onSubmit).toHaveBeenCalledWith(
                expect.objectContaining({
                    title: 'My Review Title',
                    description: 'This is a detailed description of the review.',
                    rating: 5,
                }),
                expect.anything()
            );
        });
    });

    it('cancels review', async () => {
        const onCancel = jest.fn();
        const { user } = renderWithProviders(<ReviewForm onSubmit={jest.fn()} onCancel={onCancel} />);

        await user.click(screen.getByRole('button', { name: 'review.cancel' }));
        expect(onCancel).toHaveBeenCalled();
    });
});
