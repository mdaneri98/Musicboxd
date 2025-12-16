
import uiReducer, {
    toggleSidebar,
    setSidebarOpen,
    openModal,
    closeModal,
    showConfirmation,
    hideConfirmation,
    setGlobalLoading,
    addToast,
    removeToast,
    clearAllToasts,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    setFormSubmitting,
    clearFormSubmitting,
    resetUI,
    UIState,
} from './uiSlice';

describe('uiSlice', () => {
    const initialState: UIState = {
        sidebarOpen: true,
        modal: {
            isOpen: false,
            type: null,
            data: undefined,
        },
        confirmationDialog: {
            isOpen: false,
            title: '',
            message: '',
            confirmText: 'Confirm',
            cancelText: 'Cancel',
            variant: 'info',
        },
        globalLoading: false,
        toasts: [],
        formSubmitting: {},
    };

    it('should handle initial state', () => {
        expect(uiReducer(undefined, { type: 'unknown' })).toEqual(initialState);
    });

    describe('Sidebar reducers', () => {
        it('should handle toggleSidebar', () => {
            const actual = uiReducer(initialState, toggleSidebar());
            expect(actual.sidebarOpen).toEqual(false);

            const actual2 = uiReducer(actual, toggleSidebar());
            expect(actual2.sidebarOpen).toEqual(true);
        });

        it('should handle setSidebarOpen', () => {
            const actual = uiReducer(initialState, setSidebarOpen(false));
            expect(actual.sidebarOpen).toEqual(false);
        });
    });

    describe('Modal reducers', () => {
        it('should handle openModal', () => {
            const modalData = { id: 1 };
            const actual = uiReducer(
                initialState,
                openModal({ type: 'login', data: modalData })
            );
            expect(actual.modal).toEqual({
                isOpen: true,
                type: 'login',
                data: modalData,
            });
        });

        it('should handle closeModal', () => {
            const modifiedState: UIState = {
                ...initialState,
                modal: { isOpen: true, type: 'login', data: { id: 1 } },
            };
            const actual = uiReducer(modifiedState, closeModal());
            expect(actual.modal).toEqual(initialState.modal);
        });
    });

    describe('ConfirmationDialog reducers', () => {
        it('should handle showConfirmation', () => {
            const payload = {
                title: 'Delete',
                message: 'Are you sure?',
                confirmText: 'Yes',
                variant: 'danger' as const,
            };
            const actual = uiReducer(initialState, showConfirmation(payload));
            expect(actual.confirmationDialog).toEqual({
                isOpen: true,
                title: 'Delete',
                message: 'Are you sure?',
                confirmText: 'Yes',
                cancelText: 'Cancel', // default
                variant: 'danger',
                onConfirm: undefined,
                onCancel: undefined,
            });
        });

        it('should handle hideConfirmation', () => {
            const modifiedState: UIState = {
                ...initialState,
                confirmationDialog: {
                    isOpen: true,
                    title: 'Delete',
                    message: 'Sure?',
                } as any,
            };
            const actual = uiReducer(modifiedState, hideConfirmation());
            expect(actual.confirmationDialog).toEqual(initialState.confirmationDialog);
        });
    });

    describe('GlobalLoading reducers', () => {
        it('should handle setGlobalLoading', () => {
            const actual = uiReducer(initialState, setGlobalLoading(true));
            expect(actual.globalLoading).toEqual(true);
        });
    });

    describe('Toast reducers', () => {
        it('should handle addToast', () => {
            const toast = { type: 'success' as const, message: 'Done' };
            const actual = uiReducer(initialState, addToast(toast));
            expect(actual.toasts).toHaveLength(1);
            expect(actual.toasts[0].message).toEqual('Done');
            expect(actual.toasts[0].type).toEqual('success');
            expect(actual.toasts[0].duration).toEqual(5000); // default
        });

        it('should handle removeToast', () => {
            const stateWithToast: UIState = {
                ...initialState,
                toasts: [{ id: '123', type: 'info', message: 'Hi', duration: 3000 }],
            };
            const actual = uiReducer(stateWithToast, removeToast('123'));
            expect(actual.toasts).toHaveLength(0);
        });

        it('should handle clearAllToasts', () => {
            const stateWithToasts: UIState = {
                ...initialState,
                toasts: [
                    { id: '1', type: 'info', message: 'One', duration: 1000 },
                    { id: '2', type: 'error', message: 'Two', duration: 1000 },
                ],
            };
            const actual = uiReducer(stateWithToasts, clearAllToasts());
            expect(actual.toasts).toHaveLength(0);
        });

        it('should handle showSuccess helper', () => {
            const actual = uiReducer(initialState, showSuccess('Success msg'));
            expect(actual.toasts[0].type).toEqual('success');
            expect(actual.toasts[0].message).toEqual('Success msg');
        });

        it('should handle showError helper', () => {
            const actual = uiReducer(initialState, showError('Error msg'));
            expect(actual.toasts[0].type).toEqual('error');
        });

        it('should handle showWarning helper', () => {
            const actual = uiReducer(initialState, showWarning('Warn msg'));
            expect(actual.toasts[0].type).toEqual('warning');
        });

        it('should handle showInfo helper', () => {
            const actual = uiReducer(initialState, showInfo('Info msg'));
            expect(actual.toasts[0].type).toEqual('info');
        });
    });

    describe('FormSubmitting reducers', () => {
        it('should handle setFormSubmitting', () => {
            const actual = uiReducer(
                initialState,
                setFormSubmitting({ formId: 'login', isSubmitting: true })
            );
            expect(actual.formSubmitting['login']).toEqual(true);
        });

        it('should handle clearFormSubmitting', () => {
            const state: UIState = {
                ...initialState,
                formSubmitting: { login: true },
            };
            const actual = uiReducer(state, clearFormSubmitting('login'));
            expect(actual.formSubmitting['login']).toBeUndefined();
        });
    });

    describe('Reset', () => {
        it('should handle resetUI', () => {
            const modifiedState: UIState = {
                ...initialState,
                sidebarOpen: false,
                globalLoading: true,
                toasts: [{ id: '1', type: 'error', message: 'err' }],
            };
            const actual = uiReducer(modifiedState, resetUI());
            expect(actual).toEqual(initialState);
        });
    });
});
