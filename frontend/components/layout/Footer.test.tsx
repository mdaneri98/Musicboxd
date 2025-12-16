import React from 'react';
import { screen } from '@testing-library/react';
import Footer from './Footer';
import { renderWithProviders } from '../../test-utils';

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string, options: any) => {
            if (key === 'footer.copyright') {
                return `Copyright ${options.year}`;
            }
            return key;
        },
    }),
}));

describe('Footer', () => {
    it('renders copyright with current year', () => {
        renderWithProviders(<Footer />);
        const currentYear = new Date().getFullYear();
        expect(screen.getByText(`Copyright ${currentYear}`)).toBeInTheDocument();
    });
});
