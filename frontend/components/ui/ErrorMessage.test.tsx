import React from 'react';
import { screen } from '@testing-library/react';
import { ErrorMessage } from './ErrorMessage';
import { renderWithProviders } from '../../test-utils';

describe('ErrorMessage', () => {
    it('renders nothing when message is falsy', () => {
        const { container } = renderWithProviders(<ErrorMessage message={null} />);
        expect(container.firstChild).toBeNull();
    });

    it('renders correctly with message and default title', () => {
        renderWithProviders(<ErrorMessage message="Something went wrong" />);

        expect(screen.getByRole('alert')).toBeInTheDocument();
        expect(screen.getByText('Error')).toBeInTheDocument();
        expect(screen.getByText('Something went wrong')).toBeInTheDocument();
        expect(screen.queryByRole('button')).not.toBeInTheDocument();
    });

    it('renders with custom title', () => {
        renderWithProviders(
            <ErrorMessage message="Failed" title="Custom Title" />
        );

        expect(screen.getByText('Custom Title')).toBeInTheDocument();
    });

    it('renders retry button and handles click when onRetry is provided', async () => {
        const handleRetry = jest.fn();
        const { user } = renderWithProviders(
            <ErrorMessage message="Failed" onRetry={handleRetry} />
        );

        const button = screen.getByRole('button', { name: /tryAgain/i });
        expect(button).toBeInTheDocument();

        await user.click(button);
        expect(handleRetry).toHaveBeenCalledTimes(1);
    });
});
