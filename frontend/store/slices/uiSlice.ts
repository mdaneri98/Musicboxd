/**
 * UI Slice
 * Redux slice for UI state management (modals, dialogs, sidebar, global loading, etc.)
 */

import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface ConfirmationDialog {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm?: () => void;
  onCancel?: () => void;
  confirmText?: string;
  cancelText?: string;
  variant?: 'danger' | 'warning' | 'info';
}

export interface Modal {
  isOpen: boolean;
  type:
  | 'login'
  | 'register'
  | 'review'
  | 'comment'
  | 'profile'
  | 'artist'
  | 'album'
  | 'song'
  | 'settings'
  | null;
  data?: any;
}

export interface Toast {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  duration?: number; // milliseconds, 0 = no auto-dismiss
}

export interface UIState {
  // Sidebar state
  sidebarOpen: boolean;
  // Modal state
  modal: Modal;
  // Confirmation dialog state
  confirmationDialog: ConfirmationDialog;
  // Global loading state (for page-level loading)
  globalLoading: boolean;
  // Toast notifications
  toasts: Toast[];
  // Form submission states (keyed by form ID)
  formSubmitting: Record<string, boolean>;
}

// ============================================================================
// Initial State
// ============================================================================

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

// ============================================================================
// Slice
// ============================================================================

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    /**
     * Toggle sidebar
     */
    toggleSidebar: (state) => {
      state.sidebarOpen = !state.sidebarOpen;
    },

    /**
     * Set sidebar open/closed
     */
    setSidebarOpen: (state, action: PayloadAction<boolean>) => {
      state.sidebarOpen = action.payload;
    },

    /**
     * Open modal
     */
    openModal: (
      state,
      action: PayloadAction<{
        type: Modal['type'];
        data?: any;
      }>
    ) => {
      state.modal = {
        isOpen: true,
        type: action.payload.type,
        data: action.payload,
      };
    },

    /**
     * Close modal
     */
    closeModal: (state) => {
      state.modal = {
        isOpen: false,
        type: null,
        data: undefined,
      };
    },

    /**
     * Show confirmation dialog
     */
    showConfirmation: (
      state,
      action: PayloadAction<{
        title: string;
        message: string;
        onConfirm?: () => void;
        onCancel?: () => void;
        confirmText?: string;
        cancelText?: string;
        variant?: 'danger' | 'warning' | 'info';
      }>
    ) => {
      state.confirmationDialog = {
        isOpen: true,
        title: action.payload.title,
        message: action.payload.message,
        onConfirm: action.payload.onConfirm,
        onCancel: action.payload.onCancel,
        confirmText: action.payload.confirmText || 'Confirm',
        cancelText: action.payload.cancelText || 'Cancel',
        variant: action.payload.variant || 'info',
      };
    },

    /**
     * Hide confirmation dialog
     */
    hideConfirmation: (state) => {
      state.confirmationDialog = {
        isOpen: false,
        title: '',
        message: '',
        confirmText: 'Confirm',
        cancelText: 'Cancel',
        variant: 'info',
      };
    },

    /**
     * Set global loading
     */
    setGlobalLoading: (state, action: PayloadAction<boolean>) => {
      state.globalLoading = action.payload;
    },

    /**
     * Add toast notification
     */
    addToast: (
      state,
      action: PayloadAction<{
        type: Toast['type'];
        message: string;
        duration?: number;
      }>
    ) => {
      const id = `toast-${Date.now()}-${Math.random()}`;
      state.toasts.push({
        id,
        type: action.payload.type,
        message: action.payload.message,
        duration: action.payload.duration ?? 5000, // Default 5 seconds
      });
    },

    /**
     * Remove toast notification
     */
    removeToast: (state, action: PayloadAction<string>) => {
      state.toasts = state.toasts.filter((toast) => toast.id !== action.payload);
    },

    /**
     * Clear all toasts
     */
    clearAllToasts: (state) => {
      state.toasts = [];
    },

    /**
     * Show success toast
     */
    showSuccess: (state, action: PayloadAction<string>) => {
      const id = `toast-${Date.now()}-${Math.random()}`;
      state.toasts.push({
        id,
        type: 'success',
        message: action.payload,
        duration: 5000,
      });
    },

    /**
     * Show error toast
     */
    showError: (state, action: PayloadAction<string>) => {
      const id = `toast-${Date.now()}-${Math.random()}`;
      state.toasts.push({
        id,
        type: 'error',
        message: action.payload,
        duration: 7000, // Errors stay longer
      });
    },

    /**
     * Show warning toast
     */
    showWarning: (state, action: PayloadAction<string>) => {
      const id = `toast-${Date.now()}-${Math.random()}`;
      state.toasts.push({
        id,
        type: 'warning',
        message: action.payload,
        duration: 6000,
      });
    },

    /**
     * Show info toast
     */
    showInfo: (state, action: PayloadAction<string>) => {
      const id = `toast-${Date.now()}-${Math.random()}`;
      state.toasts.push({
        id,
        type: 'info',
        message: action.payload,
        duration: 5000,
      });
    },

    /**
     * Set form submitting state
     */
    setFormSubmitting: (
      state,
      action: PayloadAction<{
        formId: string;
        isSubmitting: boolean;
      }>
    ) => {
      state.formSubmitting[action.payload.formId] = action.payload.isSubmitting;
    },

    /**
     * Clear form submitting state
     */
    clearFormSubmitting: (state, action: PayloadAction<string>) => {
      delete state.formSubmitting[action.payload];
    },

    /**
     * Reset UI state
     */
    resetUI: (state) => {
      state.sidebarOpen = true;
      state.modal = {
        isOpen: false,
        type: null,
        data: undefined,
      };
      state.confirmationDialog = {
        isOpen: false,
        title: '',
        message: '',
        confirmText: 'Confirm',
        cancelText: 'Cancel',
        variant: 'info',
      };
      state.globalLoading = false;
      state.toasts = [];
      state.formSubmitting = {};
    },
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const {
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
} = uiSlice.actions;

// Selectors
export const selectSidebarOpen = (state: RootState) => state.ui.sidebarOpen;
export const selectModal = (state: RootState) => state.ui.modal;
export const selectConfirmationDialog = (state: RootState) => state.ui.confirmationDialog;
export const selectGlobalLoading = (state: RootState) => state.ui.globalLoading;
export const selectToasts = (state: RootState) => state.ui.toasts;
export const selectFormSubmitting = (formId: string) => (state: RootState) =>
  state.ui.formSubmitting[formId] || false;

export default uiSlice.reducer;

