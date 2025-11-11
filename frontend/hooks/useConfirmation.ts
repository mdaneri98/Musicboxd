import { useState, useCallback } from 'react';

/**
 * Custom hook for managing confirmation dialogs
 * Simplifies the pattern of asking for user confirmation before actions
 * 
 * Best Practice: Encapsulates confirmation dialog state and provides
 * a clean API for confirm/cancel actions
 * 
 * @returns Object with confirmation state and handlers
 * 
 * @example
 * const { isOpen, confirm, cancel, data } = useConfirmation<{ userId: number }>();
 * 
 * const handleDelete = async () => {
 *   const confirmed = await confirm({ userId: 123 });
 *   if (confirmed) {
 *     // Perform delete action
 *   }
 * };
 */
interface UseConfirmationReturn<T = unknown> {
  isOpen: boolean;
  data: T | null;
  confirm: (data?: T) => Promise<boolean>;
  cancel: () => void;
}

export function useConfirmation<T = unknown>(): UseConfirmationReturn<T> {
  const [isOpen, setIsOpen] = useState(false);
  const [data, setData] = useState<T | null>(null);
  const [resolvePromise, setResolvePromise] = useState<((value: boolean) => void) | null>(null);

  const confirm = useCallback((confirmData?: T): Promise<boolean> => {
    return new Promise((resolve) => {
      setData(confirmData || null);
      setIsOpen(true);
      setResolvePromise(() => resolve);
    });
  }, []);

  const handleConfirm = useCallback(() => {
    if (resolvePromise) {
      resolvePromise(true);
    }
    setIsOpen(false);
    setData(null);
    setResolvePromise(null);
  }, [resolvePromise]);

  const handleCancel = useCallback(() => {
    if (resolvePromise) {
      resolvePromise(false);
    }
    setIsOpen(false);
    setData(null);
    setResolvePromise(null);
  }, [resolvePromise]);

  return {
    isOpen,
    data,
    confirm,
    cancel: handleCancel,
    // Internal handlers for the confirmation modal
    _handleConfirm: handleConfirm,
    _handleCancel: handleCancel,
  } as UseConfirmationReturn<T> & {
    _handleConfirm: () => void;
    _handleCancel: () => void;
  };
}

