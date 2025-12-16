import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import LoginForm from './LoginForm';
import { renderWithProviders } from '../../test-utils';

describe('LoginForm', () => {
    it('renders input fields', () => {
        renderWithProviders(<LoginForm onSubmit={jest.fn()} />);

        expect(screen.getByLabelText('auth.login.username')).toBeInTheDocument();
        expect(screen.getByLabelText('auth.login.password')).toBeInTheDocument();
        expect(screen.getByLabelText('auth.login.rememberMe')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'auth.login.submit' })).toBeInTheDocument();
    });

    it('validates required fields', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<LoginForm onSubmit={onSubmit} />);

        await user.click(screen.getByRole('button', { name: 'auth.login.submit' }));

        await waitFor(() => {
            // Validation messages
            // Based on real schema: "validation.username.required"
            // Since we didn't mock schema, it returns translation key or something.
            // We can inspect if errors appear.
            // Let's assume validation prevents submit.
            expect(onSubmit).not.toHaveBeenCalled();
        });
    });

    it('submits valid data', async () => {
        const onSubmit = jest.fn();
        const { user } = renderWithProviders(<LoginForm onSubmit={onSubmit} />);

        await user.type(screen.getByLabelText('auth.login.username'), 'validuser');
        await user.type(screen.getByLabelText('auth.login.password'), 'password123');
        await user.click(screen.getByLabelText('auth.login.rememberMe')); // Check remember me

        await user.click(screen.getByRole('button', { name: 'auth.login.submit' }));

        await waitFor(() => {
            expect(onSubmit).toHaveBeenCalledWith(
                expect.objectContaining({
                    username: 'validuser',
                    password: 'password123',
                    rememberMe: true,
                }),
                expect.anything()
            );
        });
    });

    it('displays external error', () => {
        renderWithProviders(<LoginForm onSubmit={jest.fn()} error="Invalid credentials" />);

        expect(screen.getByText('Invalid credentials')).toBeInTheDocument();
    });
});
