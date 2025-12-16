import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import ConfirmationModal from './ConfirmationModal';
import { renderWithProviders } from '../../test-utils';

describe('ConfirmationModal', () => {
    const defaultProps = {
        isOpen: true,
        message: 'Are you sure?',
        onConfirm: jest.fn(),
        onCancel: jest.fn(),
        confirmText: 'Yes',
        cancelText: 'No',
    };

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('renders nothing when isOpen is false', () => {
        const { container } = renderWithProviders(
            <ConfirmationModal {...defaultProps} isOpen={false} />
        );
        expect(container.firstChild).toBeNull();
    });

    it('renders correctly when isOpen is true', () => {
        renderWithProviders(<ConfirmationModal {...defaultProps} />);

        expect(screen.getByText('Are you sure?')).toBeInTheDocument();
        expect(screen.getByText('Yes')).toBeInTheDocument();
        expect(screen.getByText('No')).toBeInTheDocument();
    });

    it('calls onConfirm when confirm button is clicked', async () => {
        const { user } = renderWithProviders(<ConfirmationModal {...defaultProps} />);

        await user.click(screen.getByText('Yes'));
        expect(defaultProps.onConfirm).toHaveBeenCalledTimes(1);
        expect(defaultProps.onCancel).not.toHaveBeenCalled();
    });

    it('calls onCancel when cancel button is clicked', async () => {
        const { user } = renderWithProviders(<ConfirmationModal {...defaultProps} />);

        await user.click(screen.getByText('No'));
        expect(defaultProps.onCancel).toHaveBeenCalledTimes(1);
        expect(defaultProps.onConfirm).not.toHaveBeenCalled();
    });

    it('calls onCancel when overlay is clicked', () => {
        // Note: userEvent.click might be tricky with overlay if elements are covered
        // using fireEvent for direct overlay click as it's checking strictly the event target logic
        const { container } = renderWithProviders(<ConfirmationModal {...defaultProps} />);

        // The overlay is the first div
        const overlay = container.firstChild as HTMLElement;
        fireEvent.click(overlay);

        expect(defaultProps.onCancel).toHaveBeenCalledTimes(1);
    });

    it('does not call onCancel when modal content is clicked', () => {
        renderWithProviders(<ConfirmationModal {...defaultProps} />);

        const content = screen.getByText('Are you sure?');
        fireEvent.click(content);

        expect(defaultProps.onCancel).not.toHaveBeenCalled();
    });
});
