import React from 'react';
import { screen } from '@testing-library/react';
import Layout from './Layout';
import { renderWithProviders } from '../../test-utils';

jest.mock('./Sidebar', () => () => <div data-testid="sidebar">Sidebar</div>);
jest.mock('./Footer', () => () => <div data-testid="footer">Footer</div>);
jest.mock('@/components/ui', () => ({
    ToastContainer: () => <div data-testid="toast-container">Toasts</div>,
}));
jest.mock('./Head', () => ({ title }: { title: string }) => <div data-testid="head">{title}</div>);

describe('Layout', () => {
    it('renders structure with defaults', () => {
        renderWithProviders(
            <Layout title="Test Page">
                <div data-testid="child-content">Content</div>
            </Layout>
        );

        expect(screen.getByTestId('head')).toHaveTextContent('Test Page');
        expect(screen.getByTestId('sidebar')).toBeInTheDocument();
        expect(screen.getByTestId('child-content')).toHaveTextContent('Content');
        expect(screen.getByTestId('footer')).toBeInTheDocument();
        expect(screen.getByTestId('toast-container')).toBeInTheDocument();
    });

    it('hides sidebar and footer when requested', () => {
        renderWithProviders(
            <Layout title="Test Page" showSidebar={false} showFooter={false}>
                <div>Content</div>
            </Layout>
        );

        expect(screen.queryByTestId('sidebar')).not.toBeInTheDocument();
        expect(screen.queryByTestId('footer')).not.toBeInTheDocument();
        expect(screen.getByTestId('toast-container')).toBeInTheDocument();
    });
});
