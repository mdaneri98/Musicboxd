import React from 'react';
import { screen, fireEvent, waitFor } from '@testing-library/react';
import EditProfileForm from './EditProfileForm';
import { renderWithProviders } from '../../test-utils';

jest.mock('@/utils/validationSchemas', () => ({
    editProfileSchema: {
        validate: jest.fn(),
        // Mock yup schema object structure if needed by resolver
        // But yupResolver expects a yup schema. 
        // Providing a real schema is safer or wemock the resolver itself.
        // Since we want to test that errors are displayed, using REAL schema is better.
        // So I will UNMOCK it by not mocking it at all, or mock return values if too complex.
        // For now, I will use the REAL schema imported from utils.
    }
}));
jest.unmock('@/utils/validationSchemas');

describe('EditProfileForm', () => {
    const initialValues = {
        name: 'Initial Name',
        username: 'user1',
        bio: 'Initial Bio',
    };

    it('renders initial values', () => {
        renderWithProviders(<EditProfileForm onSubmit={jest.fn()} initialValues={initialValues} />);

        expect(screen.getByDisplayValue('Initial Name')).toBeInTheDocument();
        expect(screen.getByDisplayValue('user1')).toBeInTheDocument();
        expect(screen.getByDisplayValue('Initial Bio')).toBeInTheDocument();

        // Username should be disabled
        expect(screen.getByDisplayValue('user1')).toBeDisabled();
    });

    it('submits updated values', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<EditProfileForm onSubmit={onSubmit} initialValues={initialValues} />);

        const nameInput = screen.getByDisplayValue('Initial Name');
        await user.clear(nameInput);
        await user.type(nameInput, 'New Name');

        await user.click(screen.getByRole('button', { name: 'profile.update' }));

        await waitFor(() => {
            expect(onSubmit).toHaveBeenCalledWith(
                expect.objectContaining({
                    name: 'New Name',
                    username: 'user1',
                    bio: 'Initial Bio',
                }),
                expect.anything()
            );
        });
    });

    it('handles image selection', async () => {
        const onImageChange = jest.fn();
        const { user } = renderWithProviders(
            <EditProfileForm onSubmit={jest.fn()} initialValues={initialValues} onImageChange={onImageChange} />
        );

        const file = new File(['(⌐□_□)'], 'chucknorris.png', { type: 'image/png' });

        // The input is hidden: style={{ display: 'none' }}
        // we can verify looking by ID or creating specific query
        // But simpler to fireEvent on the hidden input if we can find it
        // Or trigger the click on the image which opens the file dialog (hard to test dialog).
        // Best practice for file input:
        const input = document.getElementById('userImageInput') as HTMLInputElement;

        // In JSDOM, we can verify it exists.
        // To simulate upload:
        // user.upload(input, file) is preferred.
        // But input is hidden. UserEvent might refuse to interact with hidden input.
        // So we use fireEvent.change

        fireEvent.change(input, { target: { files: [file] } });

        expect(onImageChange).toHaveBeenCalled();
        expect(onImageChange.mock.calls[0][0]).toBe(file);
    });
});
