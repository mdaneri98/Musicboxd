import React from 'react';
import { screen } from '@testing-library/react';
import LanguageSwitcher from './LanguageSwitcher';
import { renderWithProviders } from '../../test-utils';
import { Language } from '@/types/enums';

describe('LanguageSwitcher', () => {
    it('renders correctly', () => {
        // We expect the mocked translation keys
        renderWithProviders(<LanguageSwitcher />);

        expect(screen.getByText('settings.language')).toBeInTheDocument();
        expect(screen.getByText('settings.languageDescription')).toBeInTheDocument();
    });

    it('calls onLanguageChange when selection changes', async () => {
        const handleChange = jest.fn();
        const { user } = renderWithProviders(<LanguageSwitcher onLanguageChange={handleChange} />);

        // The select usually has an ID or we can get by role combobox if unique
        const select = screen.getByRole('combobox'); // Assuming it's the only one or we'd use label

        await user.selectOptions(select, Language.ES);

        expect(handleChange).toHaveBeenCalledWith(Language.ES);
    });
});
