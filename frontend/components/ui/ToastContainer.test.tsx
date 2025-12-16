import React from 'react';
import { screen, fireEvent } from '@testing-library/react';
import ToastContainer from './ToastContainer';
import { renderWithProviders } from '../../test-utils';
import { removeToast } from '@/store/slices/uiSlice';



describe('ToastContainer', () => {
    const mockToasts = [
        { id: '1', message: 'Success!', type: 'success' as const, duration: 2000 },
        { id: '2', message: 'Error!', type: 'error' as const },
    ];

    it('renders nothing when empty', () => {
        const { container } = renderWithProviders(<ToastContainer />, {
            preloadedState: {
                ui: {
                    toasts: [],
                    sidebarOpen: true,
                    modal: { isOpen: false, type: null },
                    confirmationDialog: { isOpen: false, title: '', message: '' },
                    globalLoading: false,
                    formSubmitting: {}
                } as any // Cast because real state has more fields but this should be enough if reducer merges or if we provide enough
            }
        });


        expect(container.firstChild).toBeNull();
    });

    it('renders toasts from store', () => {
        renderWithProviders(<ToastContainer />, {
            preloadedState: {
                ui: {
                    toasts: mockToasts,
                    sidebarOpen: true,
                    modal: { isOpen: false, type: null },
                    confirmationDialog: { isOpen: false, title: '', message: '' },
                    globalLoading: false,
                    formSubmitting: {}
                } as any
            }
        });

        expect(screen.getByText('Success!')).toBeInTheDocument();
        expect(screen.getByText('Error!')).toBeInTheDocument();
    });

    it('dispatches removeToast when close clicked', () => {
        const { store } = renderWithProviders(<ToastContainer />, {
            preloadedState: {
                ui: {
                    toasts: [mockToasts[0]],
                    sidebarOpen: true,
                    modal: { isOpen: false, type: null },
                    confirmationDialog: { isOpen: false, title: '', message: '' },
                    globalLoading: false,
                    formSubmitting: {}
                } as any
            }
        });

        // Click the close button
        const closeBtn = screen.getByLabelText('Close notification');
        fireEvent.click(closeBtn);

        // Check if toast is removed from store
        // This implicitly verifies that removeToast action was dispatched and handled
        expect(store.getState().ui.toasts).toHaveLength(0);
    });
});
