import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import SearchForm from './SearchForm';
import { renderWithProviders } from '../../test-utils';

jest.mock('@/types/enums', () => ({
    SearchType: { MUSIC: 'music' }
}));

describe('SearchForm', () => {
    it('renders inputs', () => {
        renderWithProviders(<SearchForm onSubmit={jest.fn()} />);

        expect(screen.getByPlaceholderText('search.placeholder')).toBeInTheDocument();
        expect(screen.getByRole('combobox')).toBeInTheDocument();
        expect(screen.getByRole('button')).toBeInTheDocument();
    });

    it('validates empty search', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<SearchForm onSubmit={onSubmit} />);

        await user.click(screen.getByRole('button'));

        await waitFor(() => {
            expect(onSubmit).not.toHaveBeenCalled();
        });
    });

    it('submits valid search', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<SearchForm onSubmit={onSubmit} />);

        await user.type(screen.getByPlaceholderText('search.placeholder'), 'Queen');
        await user.selectOptions(screen.getByRole('combobox'), 'artists');
        await user.click(screen.getByRole('button'));

        await waitFor(() => {
            expect(onSubmit).toHaveBeenCalledWith(
                expect.objectContaining({
                    query: 'Queen',
                    type: 'artists'
                }),
                expect.anything()
            );
        });
    });
});
