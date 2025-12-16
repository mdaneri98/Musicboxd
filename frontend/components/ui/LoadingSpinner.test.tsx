import React from 'react';
import { screen } from '@testing-library/react';
import { LoadingSpinner } from './LoadingSpinner';
import { renderWithProviders } from '../../test-utils';

describe('LoadingSpinner', () => {
    it('renders default spinner correctly', () => {
        renderWithProviders(<LoadingSpinner />);

        const status = screen.getByRole('status');
        expect(status).toBeInTheDocument();
        // Default size is medium, so no extra class usually, but let's check structure
        expect(status.querySelector('.spinner')).toBeInTheDocument();
    });

    it('renders with message', () => {
        renderWithProviders(<LoadingSpinner message="Loading data..." />);

        expect(screen.getByText('Loading data...')).toBeInTheDocument();
    });

    it('renders with specific size', () => {
        const { container } = renderWithProviders(<LoadingSpinner size="large" />);

        // We check closely if the class is applied.
        // Based on implementation: sizeClasses[size], so 'spinner-large'
        // It's inside the inner div usually?
        // The implementation wraps spinner in a div with className.
        const wrapper = container.querySelector('.spinner-large');
        expect(wrapper).toBeInTheDocument();
    });

    it('renders centered', () => {
        const { container } = renderWithProviders(<LoadingSpinner centered />);

        expect(container.querySelector('.loading-center')).toBeInTheDocument();
    });

    it('renders fullscreen', () => {
        const { container } = renderWithProviders(<LoadingSpinner fullScreen />);

        expect(container.querySelector('.loading-container')).toBeInTheDocument();
    });

    it('renders inline', () => {
        const { container } = renderWithProviders(<LoadingSpinner inline />);

        expect(container.querySelector('.inline')).toBeInTheDocument();
    });
});
